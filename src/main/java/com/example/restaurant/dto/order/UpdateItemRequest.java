// UpdateItemRequest.java
package com.example.restaurant.dto.order;
import jakarta.validation.constraints.Min;

public record UpdateItemRequest(@Min(1) Integer quantity, String note) {}
