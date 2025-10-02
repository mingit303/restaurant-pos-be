package com.example.restaurant.domain.table;

public enum TableStatus {                        
    FREE,           //Bàn trống
    RESERVED,       //Đã giữ cho reservation (khách chưa ngồi)
    OCCUPIED,       // Đang có khách
    CLEANING,       //Nhân viên đang dọn
    OUT_OF_SERVICE  //Bàn hỏng/không sử dụng
}
