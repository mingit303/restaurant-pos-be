package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ChangeTableRequest {
    @NotNull
    private Long newTableId;
}