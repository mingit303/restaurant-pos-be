package com.example.restaurant.repository.reservation;      // 1

import com.example.restaurant.domain.reservation.*;          // 2: Reservation + Status
import org.springframework.data.jpa.repository.*;            // 3
import org.springframework.data.repository.query.Param;      // 4
import java.time.*;                                          // 5
import java.util.*;                                          // 6

public interface ReservationRepository extends JpaRepository<Reservation, Long> { // 7

    // 8: Tìm reservation đang "active" của cùng 1 bàn và overlap với khoảng [windowStart, windowEnd]
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.table.id = :tableId
          AND r.status IN (:active)
          AND r.startTime < :windowEnd
          AND r.endTime   > :windowStart
    """)
    List<Reservation> findOverlaps(
            @Param("tableId") Long tableId,                 // 9
            @Param("active") List<ReservationStatus> active,
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd
    );

    // 10: Lấy các reservation có thể "EXPIRE" (PENDING/CONFIRMED) khi đã quá 15' sau start
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.status IN ('PENDING','CONFIRMED')
          AND r.startTime <= :threshold
    """)
    List<Reservation> findExpiredCandidates(@Param("threshold") LocalDateTime threshold);
}
