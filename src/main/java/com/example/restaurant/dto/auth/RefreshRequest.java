package com.example.restaurant.dto.auth;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}