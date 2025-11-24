package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StockInRequest {
    @NotNull @Positive
    private Double amount;

    // private String note;
}
