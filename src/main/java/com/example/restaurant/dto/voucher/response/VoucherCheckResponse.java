package com.example.restaurant.dto.voucher.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class VoucherCheckResponse {
    private boolean valid;
    private String message;
    private String code;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscount;

    public VoucherCheckResponse(){}
    public VoucherCheckResponse(boolean valid,String message,String code,BigDecimal discountPercent,BigDecimal maxDiscount){
        this.valid = valid; 
        this.message = message;
        this.code = code;
        this.discountPercent = discountPercent;
        this.maxDiscount = maxDiscount;
    }
}