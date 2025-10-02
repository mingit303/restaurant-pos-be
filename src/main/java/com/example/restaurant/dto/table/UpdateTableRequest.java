package com.example.restaurant.dto.table;

import jakarta.validation.constraints.*;

public record UpdateTableRequest(
        @NotBlank @Size(max = 20) String code,
        @NotNull @Min(1) Integer capacity
) {}
