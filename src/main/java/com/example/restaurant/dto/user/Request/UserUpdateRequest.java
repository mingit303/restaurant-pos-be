package com.example.restaurant.dto.user.Request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String role;
    private String status;
}
