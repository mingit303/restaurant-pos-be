package com.example.restaurant.dto.voucher.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VoucherRequest {
    @NotBlank @Size(max = 50)
    private String code;

    @Size(max = 255)
    private String description;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal discountPercent;

    @DecimalMin("0.0")
    private BigDecimal maxDiscount; // nullable

    private LocalDate startDate; // nullable
    private LocalDate endDate;   // nullable

    @Min(1)
    private Integer usageLimit; // nullable

    private Boolean active; // nullable
}
