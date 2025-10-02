package com.example.restaurant.scheduler;                       // 1

import com.example.restaurant.service.ReservationService;       // 2
import org.springframework.scheduling.annotation.EnableScheduling; // 3
import org.springframework.scheduling.annotation.Scheduled;     // 4
import org.springframework.stereotype.Component;                // 5

@Component                                                     // 6
@EnableScheduling                                              // 7: bật scheduler
public class ReservationExpiryScheduler {

    private final ReservationService reservationService;        // 8

    public ReservationExpiryScheduler(ReservationService rs) {  // 9
        this.reservationService = rs;
    }

    // 10: Chạy mỗi 60s để expire reservation trễ > 15'
    @Scheduled(fixedRate = 60_000)                              // 11
    public void runExpireJob() {
        reservationService.expireOverdueReservations();
        // int changed = reservationService.expireOverdueReservations(); // 12
        // Có thể log: System.out.println("Expired: " + changed);
    }
}
