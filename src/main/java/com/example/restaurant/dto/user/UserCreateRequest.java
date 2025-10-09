package com.example.restaurant.dto.user;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String password;
    private String role;     // ROLE_ADMIN, ROLE_WAITER,...
}
