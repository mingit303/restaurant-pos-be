package com.example.restaurant.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserStatusRequest(
    @NotBlank String status
) {}
