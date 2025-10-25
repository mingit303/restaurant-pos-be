package com.example.restaurant.dto.voucher.response;

import java.math.BigDecimal;

public class VoucherCheckResponse {
    private boolean valid;
    private String message;
    private String code;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscount;

    public VoucherCheckResponse(){}
    public VoucherCheckResponse(boolean valid,String message,String code,BigDecimal discountPercent,BigDecimal maxDiscount){
        this.valid=valid;this.message=message;this.code=code;this.discountPercent=discountPercent;this.maxDiscount=maxDiscount;
    }
    // getters/setters
    public boolean isValid(){return valid;} public void setValid(boolean v){this.valid=v;}
    public String getMessage(){return message;} public void setMessage(String v){this.message=v;}
    public String getCode(){return code;} public void setCode(String v){this.code=v;}
    public BigDecimal getDiscountPercent(){return discountPercent;} public void setDiscountPercent(BigDecimal v){this.discountPercent=v;}
    public BigDecimal getMaxDiscount(){return maxDiscount;} public void setMaxDiscount(BigDecimal v){this.maxDiscount=v;}
}