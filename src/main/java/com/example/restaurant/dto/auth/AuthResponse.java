package com.example.restaurant.dto.auth;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AuthResponse {
    private String username;
    private String roleName;
    private String accessToken;
    private String refreshToken;
}
