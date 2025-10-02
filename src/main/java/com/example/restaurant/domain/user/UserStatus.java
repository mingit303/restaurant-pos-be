package com.example.restaurant.domain.user;

public enum UserStatus {
    PENDING("Tài khoản chưa kích hoạt."),
    SUSPENDED("Tài khoản đang bị tạm khóa."),
    DISABLED("Tài khoản đã bị khóa vĩnh viễn."),
    ACTIVE(null);

    private final String message;
    UserStatus(String message) { this.message = message; }
    public String getMessage() { return message; }
}