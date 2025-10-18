package com.example.restaurant.domain.order;

public enum OrderStatus {
    PENDING,   // tạo xong, chờ xác nhận
    CONFIRMED, // chốt món
    SERVED,    // đã phục vụ
    PAID,     // đã thanh toán (GĐ4 mock, GĐ8 thật)
    CANCELLED,
}