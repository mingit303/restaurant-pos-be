package com.example.restaurant.dto.voucher.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    // getters/setters
    public String getCode(){return code;} public void setCode(String v){this.code=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public BigDecimal getDiscountPercent(){return discountPercent;} public void setDiscountPercent(BigDecimal v){this.discountPercent=v;}
    public BigDecimal getMaxDiscount(){return maxDiscount;} public void setMaxDiscount(BigDecimal v){this.maxDiscount=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){this.startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){this.endDate=v;}
    public Integer getUsageLimit(){return usageLimit;} public void setUsageLimit(Integer v){this.usageLimit=v;}
    public Boolean getActive(){return active;} public void setActive(Boolean v){this.active=v;}
}
