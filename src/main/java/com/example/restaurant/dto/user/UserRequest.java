package com.example.restaurant.dto.user;

public record UserRequest(
    String username,
    String password,
    java.util.Set<String> roles
) {}
