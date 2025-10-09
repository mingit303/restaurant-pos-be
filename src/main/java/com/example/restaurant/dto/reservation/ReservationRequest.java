package com.example.restaurant.dto.reservation;          // 1

import jakarta.validation.constraints.*;                 // 2: Bean Validation
import java.time.*;                                      // 3

// 4: record = immutable DTO gọn nhẹ
public record ReservationRequest(
        @NotNull 
        Long tableId,                           // 5: Bắt buộc chỉ định bàn
        @NotBlank 
        String customerName,                   // 6: Tên khách
        @Size(max = 20) 
        String customerPhone,            // 7: SĐT <= 20 ký tự
        @NotNull 
        LocalDateTime startTime,               // 8: Thời điểm bắt đầu
        @NotNull 
        @Min(15) @Max(300) 
        Integer durationMinutes, // 9: 15..300 phút
        @NotNull @Min(1) 
        Integer partySize               // 10: >=1 khách
) {}
