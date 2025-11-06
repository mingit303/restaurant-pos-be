package com.example.restaurant.dto.user.Request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}