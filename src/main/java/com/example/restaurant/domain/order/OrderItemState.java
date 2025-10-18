package com.example.restaurant.domain.order;

public enum OrderItemState {
    PENDING,        // Chờ bếp
    IN_PROGRESS,    // Bếp đang nấu
    DONE,           // Bếp nấu xong
    SERVED          // Phục vụ đã mang ra bàn
}

