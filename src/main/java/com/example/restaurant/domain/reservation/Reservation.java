package com.example.restaurant.domain.reservation;          // 1

import com.example.restaurant.domain.table.RestaurantTable; // 2: Liên kết tới Bàn
import com.example.restaurant.domain.user.User;

import jakarta.persistence.*;                                // 3
import lombok.*;                                             // 4
import java.time.*;                                          // 5

@Entity                                                      // 6
@Table(name = "reservations")                                // 7
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 8
public class Reservation {                                   // 9

    @Id                                                     // 10
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // 11
    private Long id;                                        // 12

    @ManyToOne(optional = false, fetch = FetchType.LAZY)    // 13: Nhiều reservation → 1 table
    @JoinColumn(name = "table_id", nullable = false)        // 14: FK column tên table_id
    private RestaurantTable table;                          // 15

    @Column(nullable = false, length = 100)                 // 16
    private String customerName;                            // 17

    @Column(length = 20)                                    // 18
    private String customerPhone;                           // 19

    @Column(nullable = false)                               // 20
    private LocalDateTime startTime;                        // 21

    @Column(nullable = false)                               // 22
    private LocalDateTime endTime;                          // 23

    @Enumerated(EnumType.STRING)                            // 24
    @Column(nullable = false)                               // 25
    private ReservationStatus status;                       // 26

    @Column(nullable = false)                               // 27
    private Integer partySize;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;// 28

     @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}