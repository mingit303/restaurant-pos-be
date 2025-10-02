package com.example.restaurant.domain.reservation; // package reservation

// Enum trạng thái của đặt chỗ
public enum ReservationStatus {
    PENDING,    // chờ xác nhận (cashier vừa tạo)
    CONFIRMED,  // admin/manager đã xác nhận
    ARRIVED,    // khách đã đến (waiter cập nhật)
    CANCELED,   // hủy (admin/cashier)
    EXPIRED     // quá hạn 15 phút chưa đến
}
