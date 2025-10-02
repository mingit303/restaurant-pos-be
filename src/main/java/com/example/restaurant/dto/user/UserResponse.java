package com.example.restaurant.dto.user;

public record UserResponse(
    Long id,
    String username,
    java.util.Set<String> roles,
    String status
) {}