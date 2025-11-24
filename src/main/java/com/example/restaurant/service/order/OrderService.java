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
import java.util.Optional;

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

        // Nếu bàn OCCUPIED nhưng không còn order hoạt động, tự reset lại
        boolean hasActive = orderRepo.existsByTableIdAndStatusIn(
                table.getId(), List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED)
        );
        if (!hasActive && table.getStatus() == TableStatus.OCCUPIED) {
            table.setStatus(TableStatus.FREE);
            tableRepo.save(table);
        }

        // Nếu sau khi kiểm tra mà vẫn có order active thì chặn
        if (hasActive)
            throw new BadRequestException("Bàn đã có order đang phục vụ.");

        // Lấy nhân viên phục vụ
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Employee waiter = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên phục vụ."));

        // Tạo order mới
        Order order = Order.builder()
                .table(table)
                .waiter(waiter)
                .build();

        table.setStatus(TableStatus.OCCUPIED);
        tableRepo.save(table);
        orderRepo.save(order);

        tableEvents.tableChanged(table.getId(), table.getCode(), table.getCapacity(),
                table.getStatus().name(), "STATUS_CHANGED");
        orderEvents.orderChanged(order, "CREATED");

        return OrderMapper.toResponse(order);
    }


    @Transactional
    public OrderResponse addItem(Long orderId, AddItemRequest req) {
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Employee waiter = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên."));

        if (!order.getWaiter().getId().equals(waiter.getId())) {
            throw new BadRequestException("Bạn không phải người phụ trách order này.");
        }
        if (List.of(OrderStatus.CANCELLED, OrderStatus.PAID).contains(order.getStatus())) {
            throw new BadRequestException("Không thể thêm món vào đơn đã thanh toán hoặc hủy.");
        }

        if (order.getStatus() == OrderStatus.READY) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        MenuItem menu = menuRepo.findById(req.getMenuItemId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy món ăn."));

        String note = (req.getNote() == null || req.getNote().isBlank()) ? "" : req.getNote().trim();

        // Món waiter thêm luôn ở trạng thái PENDING
        OrderItemState newItemState = OrderItemState.PENDING;

        // Tìm món trùng (cùng món, cùng note, cùng state)
        Optional<OrderItem> existingOpt = order.getItems().stream()
            .filter(i ->
                i.getMenuItem().getId().equals(menu.getId()) &&
                ((i.getNote() == null ? "" : i.getNote().trim()).equalsIgnoreCase(note)) &&
                i.getState() == newItemState
            )
            .findFirst();

        if (existingOpt.isPresent()) {
            // Cộng dồn
            OrderItem existing = existingOpt.get();
            int newQty = existing.getQuantity() + req.getQuantity();
            existing.setQuantity(newQty);
            existing.setLineTotal(menu.getPrice().multiply(BigDecimal.valueOf(newQty)));
            itemRepo.save(existing);
        } else {
            // Tạo món mới (mặc định có trạng thái PENDING)
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .menuItem(menu)
                    .unitPrice(menu.getPrice())
                    .quantity(req.getQuantity())
                    .lineTotal(menu.getPrice().multiply(BigDecimal.valueOf(req.getQuantity())))
                    .note(note)
                    .state(newItemState) // 👈 quan trọng
                    .build();
            order.getItems().add(item);
            itemRepo.save(item);
        }

        recalcTotals(order);
        orderRepo.save(order);
        orderEvents.orderChanged(order, "ITEM_ADDED");

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
    public OrderResponse updateItemState(Long itemId, OrderItemState newState) {
        OrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy món."));

        //  Không cho hạ cấp món đã SERVED
        if (item.getState() == OrderItemState.SERVED && newState != OrderItemState.SERVED) {
            throw new BadRequestException("Món đã phục vụ, không thể đổi trạng thái.");
        }

        if (newState == OrderItemState.DONE) {
            if (item.getDoneAt() == null) {
                item.setDoneAt(LocalDateTime.now());
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Employee chef = employeeRepo.findByUserUsername(username)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên bếp."));
                item.setChef(chef);
            }
        } else {
            item.setDoneAt(null);
            item.setChef(null);
        }

        item.setState(newState);
        itemRepo.save(item);

        if (newState == OrderItemState.DONE) {
            recipeService.consumeFor(item.getMenuItem());
        }

        // Reload order sạch
        Order freshOrder = orderRepo.findById(item.getOrder().getId())
                .orElseThrow(() -> new NotFoundException("Order không tồn tại."));

        boolean allDone = freshOrder.getItems().stream()
                .allMatch(i -> i.getState() == OrderItemState.DONE);

        if (allDone) {
            freshOrder.setStatus(OrderStatus.READY);
            orderRepo.save(freshOrder);
            orderEvents.orderChanged(freshOrder, "ORDER_ALL_DONE");
        }

        orderEvents.orderItemChanged(item, "ITEM_STATE_UPDATED");

        return OrderMapper.toResponse(freshOrder);
    }

    @Transactional
    public OrderResponse markItemServed(Long itemId) {
        OrderItem item = itemRepo.findById(itemId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy món."));

        if (item.getState() != OrderItemState.DONE && item.getState() != OrderItemState.SERVED) {
            throw new BadRequestException("Món chưa nấu xong, không thể xác nhận phục vụ.");
        }

        item.setState(OrderItemState.SERVED);
        itemRepo.save(item);

        orderEvents.orderItemChanged(item, "ITEM_SERVED");

        // Fetch order mới hoàn toàn từ DB 
        Order freshOrder = orderRepo.findById(item.getOrder().getId())
                .orElseThrow(() -> new NotFoundException("Order không tồn tại."));

        return OrderMapper.toResponse(freshOrder);
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
                List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SERVED, OrderStatus.READY));
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
    public OrderResponse changeTable(Long orderId, Long newTableId) {
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));
        RestaurantTable oldTable = order.getTable();
        RestaurantTable newTable = tableRepo.findByIdForUpdate(newTableId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn mới."));

        if (newTable.getStatus() != TableStatus.FREE)
            throw new BadRequestException("Bàn mới không trống, không thể chuyển.");

        //  Cập nhật trạng thái bàn
        oldTable.setStatus(TableStatus.CLEANING);
        newTable.setStatus(TableStatus.OCCUPIED);

        // Chuyển order sang bàn mới
        order.setTable(newTable);
        orderRepo.save(order);
        tableRepo.saveAll(List.of(oldTable, newTable));

        // Gửi realtime update
        tableEvents.tableChanged(oldTable.getId(), oldTable.getCode(), oldTable.getCapacity(),
                oldTable.getStatus().name(), "STATUS_CHANGED");
        tableEvents.tableChanged(newTable.getId(), newTable.getCode(), newTable.getCapacity(),
                newTable.getStatus().name(), "STATUS_CHANGED");

        orderEvents.orderChanged(order, "TABLE_CHANGED");

        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse completeOrder(Long orderId) {
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));

        // Chỉ hoàn tất nếu tất cả món đều SERVED
        boolean allServed = order.getItems().stream()
                .allMatch(i -> i.getState() == OrderItemState.SERVED);

        if (!allServed) {
            throw new BadRequestException("Vẫn còn món chưa phục vụ.");
        }

        // Chuyển order sang SERVED
        order.setStatus(OrderStatus.SERVED);
        orderRepo.save(order);

        // Gửi realtime cho waiter + cashier + kitchen
        orderEvents.orderChanged(order, "ORDER_SERVED");

        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForKitchen() {
        List<Order> list = orderRepo.findByStatus(OrderStatus.CONFIRMED);
        return list.stream().map(OrderMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse markAllDone(Long orderId) {
        Order order = orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee chef = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên bếp."));

        for (OrderItem item : order.getItems()) {

            // Nếu đã SERVED rồi thì bỏ qua, không đụng vào
            if (item.getState() == OrderItemState.SERVED) {
                continue;
            }

            // Nếu đã DONE trước đó thì không trừ kho lại
            boolean wasDoneOrServedBefore =
                    item.getState() == OrderItemState.DONE || item.getState() == OrderItemState.SERVED;

            item.setState(OrderItemState.DONE);

            if (item.getDoneAt() == null) {
                item.setDoneAt(LocalDateTime.now());
            }
            item.setChef(chef);

            // Chỉ trừ kho nếu trước đó chưa DONE/SERVED
            if (!wasDoneOrServedBefore) {
                recipeService.consumeFor(item.getMenuItem());
            }
        }

        order.setStatus(OrderStatus.READY);
        orderRepo.save(order);

        orderEvents.orderChanged(order, "ORDER_ALL_DONE");

        return OrderMapper.toResponse(order);
    }
}
