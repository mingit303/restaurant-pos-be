package com.example.restaurant.dto.voucher.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
@Data
public class VoucherResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private boolean active;

    public VoucherResponse(){}
    public VoucherResponse(Long id,String code,String description,BigDecimal discountPercent,BigDecimal maxDiscount,
                           LocalDate startDate,LocalDate endDate,Integer usageLimit,Integer usedCount,boolean active){
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountPercent = discountPercent;
        this.maxDiscount = maxDiscount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.active = active;
    }
}
