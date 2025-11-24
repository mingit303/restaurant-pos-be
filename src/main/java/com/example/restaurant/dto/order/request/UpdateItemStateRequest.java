package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateItemStateRequest {
    @NotBlank private String state;
}
