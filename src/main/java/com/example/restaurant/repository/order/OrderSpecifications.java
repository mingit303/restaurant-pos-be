package com.example.restaurant.repository.order;

import com.example.restaurant.domain.order.Order;
import com.example.restaurant.domain.order.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecifications {
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, q, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
    public static Specification<Order> tableCodeLike(String code) {
        return (root, q, cb) -> (code == null || code.isBlank()) ? null
            : cb.like(cb.lower(root.join("table").get("code")), "%"+code.toLowerCase()+"%");
    }
    public static Specification<Order> waiterNameLike(String name) {
        return (root, q, cb) -> (name == null || name.isBlank()) ? null
            : cb.like(cb.lower(root.join("waiter").get("fullName")), "%"+name.toLowerCase()+"%");
    }
    public static Specification<Order> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            return from != null ? cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                                : cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }
}
