package com.example.restaurant.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String role;
    private String status;
}
