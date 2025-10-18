package com.example.restaurant.domain.table;                // 1

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;                               // 2: JPA annotations
import lombok.*;                                            // 3: Lombok annotations

@Entity                                                     // 4: Đây là Entity JPA
@Table(name = "restaurant_tables")                          // 5: Bảng DB = restaurant_tables
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder// 6: Lombok giảm boilerplate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestaurantTable {                              // 7

    @Id                                                     // 8: Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // 9: Auto-increment (MySQL)
    private Long id;                                        // 10: Khóa chính

    @Column(nullable = false, unique = true, length = 20)   // 11: Not null + Unique + dài tối đa 20
    private String code;                                    // 12: Mã bàn (T01, T02...)

    @Column(nullable = false)                               // 13
    private Integer capacity;                               // 14: Sức chứa tối đa

    @Builder.Default   
    @Enumerated(EnumType.STRING)                            // 15: Lưu enum dưới dạng TEXT
    @Column(nullable = false)                            // 16
    private TableStatus status = TableStatus.FREE;          // 17: Mặc định FREE

    @Version                                                // 18: Optimistic lock chống race khi update
    private Long version;                                   // 19
}