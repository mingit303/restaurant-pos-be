package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddItemRequest {
    @NotNull private Long menuItemId;
    @NotNull @Positive private Integer quantity;
    private String note;
}
