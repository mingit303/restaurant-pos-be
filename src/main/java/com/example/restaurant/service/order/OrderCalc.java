package com.example.restaurant.service.order;

import com.example.restaurant.domain.order.Order;
import com.example.restaurant.domain.order.OrderItem;

import java.math.BigDecimal;

public final class OrderCalc {
    private OrderCalc() {}

    public static void recalcTotals(Order o) {
        BigDecimal sub = o.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        o.setSubtotal(sub);
        if (o.getDiscount() == null) o.setDiscount(BigDecimal.ZERO);
        o.setTotal(sub.subtract(o.getDiscount()));
    }
}
