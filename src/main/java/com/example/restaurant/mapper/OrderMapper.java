package com.example.restaurant.mapper;

import com.example.restaurant.domain.order.Order;
import com.example.restaurant.domain.order.OrderItem;
import com.example.restaurant.dto.order.response.OrderResponse;
import com.example.restaurant.util.MoneyUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        if (order == null) return null;

        // Table info
        var table = Optional.ofNullable(order.getTable())
                .map(t -> new OrderResponse.TableInfo(
                        t.getId(), t.getCode(), t.getStatus().name()))
                .orElse(null);

        // Waiter info
        var waiter = Optional.ofNullable(order.getWaiter())
                .map(w -> new OrderResponse.WaiterInfo(
                        w.getId(), w.getFullName()))
                .orElse(null);

        // Items
        var items = Optional.ofNullable(order.getItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderMapper::mapItem)
                .collect(Collectors.toList());

        // System.out.println("=== Mapper debug ===");
        // System.out.println("Table: " + (order.getTable() != null));
        // System.out.println("Waiter: " + (order.getWaiter() != null));
        // System.out.println("Items: " + order.getItems().size());
        // System.out.println("Subtotal: " + order.getSubtotal());
        // System.out.println("Total: " + order.getTotal());

        return new OrderResponse(
                order.getId(), table, order.getStatus().name(), waiter,
                items, order.getSubtotal(), order.getDiscount(),
                order.getTotal(), MoneyUtils.format(order.getTotal())
        );
    }

    private static OrderResponse.Item mapItem(OrderItem i) {
        if (i == null) return null;
        var menu = i.getMenuItem();
        Long menuItemId = (menu != null) ? menu.getId() : null;
        String menuName = (menu != null) ? menu.getName() : null;

        return new OrderResponse.Item(
                i.getId(), menuItemId, menuName,
                i.getUnitPrice(), i.getQuantity(),
                i.getLineTotal(), i.getNote(),
                i.getState().name(),
                MoneyUtils.format(i.getLineTotal())
        );
    }
}
