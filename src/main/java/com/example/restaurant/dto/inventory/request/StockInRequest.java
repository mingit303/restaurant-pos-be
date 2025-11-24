package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockInRequest {
    @NotNull @Positive
    private Double amount;

}
