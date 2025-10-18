package com.example.restaurant.service.order;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.menu.MenuItem;
import com.example.restaurant.domain.order.*;
import com.example.restaurant.domain.table.*;
import com.example.restaurant.dto.order.request.*;
import com.example.restaurant.dto.order.response.OrderResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.mapper.OrderMapper;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.menu.MenuItemRepository;
import com.example.restaurant.repository.order.*;
import com.example.restaurant.repository.table.RestaurantTableRepository;
import com.example.restaurant.service.inventory.RecipeService;
import com.example.restaurant.ws.OrderEventPublisher;
import com.example.restaurant.ws.TableEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.restaurant.repository.order.OrderSpecifications.*;

@Service @RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final MenuItemRepository menuRepo;
    private final RestaurantTableRepository tableRepo;
    private final EmployeeRepository employeeRepo;
    private final OrderEventPublisher orderEvents;
    private final TableEventPublisher tableEvents;
    private final RecipeService recipeService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest req) {
        RestaurantTable table = tableRepo.findByIdForUpdate(req.getTableId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));

        boolean hasActive = orderRepo.existsByTableIdAndStatusIn(
                table.getId(), List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SERVED));
        if (hasActive) throw new BadRequestException("Bàn đã có order đang phục vụ.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Employee waiter = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên phục vụ."));

        Order order = Order.builder().table(table).waiter(waiter).build();

        table.setStatus(TableStatus.OCCUPIED);
        tableRepo.save(table);
        orderRepo.save(order);

        tableEvents.tableChanged(table.getId(), table.getCode(), table.getCapacity(), table.getStatus().name(), "STATUS_CHANGED");
        orderEvents.orderChanged(order, "CREATED");

        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse addItem(Long orderId, AddItemRequest req) {
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.PAID)
            throw new BadRequestException("Không thể thêm món vào đơn đã hủy/thanh toán.");

        MenuItem menu = menuRepo.findById(req.getMenuItemId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy món ăn."));
        BigDecimal line = menu.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));

        OrderItem item = OrderItem.builder()
                .order(order).menuItem(menu)
                .unitPrice(menu.getPrice())
                .quantity(req.getQuantity())
                .lineTotal(line)
                .note(req.getNote())
                .build();

        order.getItems().add(item);
        recalcTotals(order);
        orderRepo.save(order);

        orderEvents.orderItemChanged(item, "ITEM_ADDED");
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> search(Integer page, Integer size, String tableCode, String waiterName,
                                      String status, LocalDateTime from, LocalDateTime to) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Order> s = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            s = s.and(hasStatus(OrderStatus.valueOf(status)));
        }
        if (tableCode != null && !tableCode.isBlank()) {
            s = s.and(tableCodeLike(tableCode));
        }
        if (waiterName != null && !waiterName.isBlank()) {
            s = s.and(waiterNameLike(waiterName));
        }
        if (from != null && to != null) {
            s = s.and(createdBetween(from, to));
        }

        return orderRepo.findAll(s, pageable).map(OrderMapper::toResponse);
    }

    @Transactional
    public OrderResponse confirmOrder(Long id) {
        var order = orderRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));
        if (order.getStatus() != OrderStatus.PENDING)
            throw new BadRequestException("Chỉ order ở trạng thái PENDING mới được xác nhận.");
        if (order.getItems().isEmpty())
            throw new BadRequestException("Đơn chưa có món, không thể gửi bếp.");

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);
        orderEvents.orderChanged(order, "CONFIRMED");
        return OrderMapper.toResponse(order);
    }

    @Transactional
    public void updateItemState(Long itemId, OrderItemState newState) {
        OrderItem item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Không tìm thấy món."));
        item.setState(newState);
        itemRepo.save(item);
        if (newState == OrderItemState.DONE) recipeService.consumeFor(item.getMenuItem());
        checkAndUpdateOrderServed(item.getOrder());
        orderEvents.orderItemChanged(item, "ITEM_STATE_UPDATED");
    }

    @Transactional
    public void markItemServed(Long itemId) {
        OrderItem item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Không tìm thấy món."));
        if (item.getState() != OrderItemState.DONE)
            throw new BadRequestException("Món chưa nấu xong, không thể xác nhận phục vụ.");
        item.setState(OrderItemState.SERVED);
        itemRepo.save(item);
        checkAndUpdateOrderServed(item.getOrder());
        orderEvents.orderItemChanged(item, "ITEM_SERVED");
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("Không tìm thấy order."));

        if (order.getStatus() == OrderStatus.PAID)
            throw new BadRequestException("Không thể hủy đơn đã thanh toán.");

        boolean hasDoneOrServed = order.getItems().stream()
                .anyMatch(i -> i.getState() == OrderItemState.DONE || i.getState() == OrderItemState.SERVED);
        if (hasDoneOrServed)
            throw new BadRequestException("Không thể hủy đơn khi có món đã hoàn thành hoặc đã phục vụ.");

        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);

        RestaurantTable table = order.getTable();
        if (table != null) {
            table.setStatus(TableStatus.FREE);
            tableRepo.save(table);
            tableEvents.tableChanged(table.getId(), table.getCode(), table.getCapacity(), table.getStatus().name(), "STATUS_CHANGED");
        }
        orderEvents.orderChanged(order, "CANCELLED");
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order o = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy order."));
        return OrderMapper.toResponse(o);
    }

    @Transactional(readOnly = true)
    public OrderResponse getCurrentByTable(Long tableId) {
        var opt = orderRepo.findFirstByTableIdAndStatusIn(tableId,
                List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SERVED));
        return opt.map(OrderMapper::toResponse).orElse(null);
    }

    private void recalcTotals(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);
        order.setTotal(subtotal.subtract(order.getDiscount()!=null?order.getDiscount():BigDecimal.ZERO));
    }

    @Transactional
    public void checkAndUpdateOrderServed(Order order) {
        boolean allServed = !order.getItems().isEmpty() &&
                order.getItems().stream().allMatch(i -> i.getState()==OrderItemState.SERVED);
        if (allServed && order.getStatus()!=OrderStatus.SERVED) {
            order.setStatus(OrderStatus.SERVED);
            orderRepo.save(order);
            orderEvents.orderChanged(order, "ORDER_SERVED");
        }
    }
}
