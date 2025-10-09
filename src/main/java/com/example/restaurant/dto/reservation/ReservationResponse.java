package com.example.restaurant.dto.reservation;           // 1

import com.example.restaurant.domain.reservation.ReservationStatus; // 2
import java.time.*;                                        // 3

public record ReservationResponse(                         // 4
        Long id,                                           // 5: ID reservation
        Long tableId,                                      // 6: ID bàn
        String tableCode,                                  // 7: Mã bàn (hiển thị UI)
        String customerName,                               // 8
        String customerPhone,                              // 9
        LocalDateTime startTime,                           // 10
        LocalDateTime endTime,                             // 11
        Integer partySize,                                 // 12
        ReservationStatus status,
        Long createdById,
        String createdByUsername,
        LocalDateTime createdAt                         // 13
) {}