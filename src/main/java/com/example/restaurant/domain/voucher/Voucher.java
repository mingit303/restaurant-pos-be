package com.example.restaurant.domain.voucher;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vouchers", indexes = {
        @Index(name = "idx_voucher_code", columnList = "code", unique = true)
})
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Voucher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã voucher (duy nhất, không phân biệt hoa thường) */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    private String description;

    /** % giảm (0–100) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPercent;

    /** Giảm tối đa (VND), có thể null = không giới hạn */
    @Column(precision = 18, scale = 2)
    private BigDecimal maxDiscount;

    private LocalDate startDate;
    private LocalDate endDate;

    /** Giới hạn lượt dùng (null = không giới hạn) */
    private Integer usageLimit;

    /** Số lượt đã dùng */
    @Builder.Default
    private Integer usedCount = 0;

    /** Đang kích hoạt hay không */
    @Builder.Default
    private boolean active = true;
}
