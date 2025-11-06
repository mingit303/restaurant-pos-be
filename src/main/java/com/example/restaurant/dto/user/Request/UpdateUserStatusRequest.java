package com.example.restaurant.dto.user.Request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserStatusRequest(
    @NotBlank String status
) {}
