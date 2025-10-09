package com.example.restaurant.dto.user;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}