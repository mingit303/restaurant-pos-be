// UpdateOrderStatusRequest.java
package com.example.restaurant.dto.order;
import com.example.restaurant.domain.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
