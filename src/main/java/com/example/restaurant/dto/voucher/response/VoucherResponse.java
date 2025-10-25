package com.example.restaurant.dto.voucher.response;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        this.id=id;this.code=code;this.description=description;this.discountPercent=discountPercent;this.maxDiscount=maxDiscount;
        this.startDate=startDate;this.endDate=endDate;this.usageLimit=usageLimit;this.usedCount=usedCount;this.active=active;
    }
    // getters/setters
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public String getCode(){return code;} public void setCode(String v){this.code=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public BigDecimal getDiscountPercent(){return discountPercent;} public void setDiscountPercent(BigDecimal v){this.discountPercent=v;}
    public BigDecimal getMaxDiscount(){return maxDiscount;} public void setMaxDiscount(BigDecimal v){this.maxDiscount=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){this.startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){this.endDate=v;}
    public Integer getUsageLimit(){return usageLimit;} public void setUsageLimit(Integer v){this.usageLimit=v;}
    public Integer getUsedCount(){return usedCount;} public void setUsedCount(Integer v){this.usedCount=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){this.active=v;}
}
