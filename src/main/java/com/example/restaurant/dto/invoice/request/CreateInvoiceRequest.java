package com.example.restaurant.dto.invoice.request;

import jakarta.validation.constraints.NotNull;

public class CreateInvoiceRequest {
    @NotNull private Long orderId;
    @NotNull private String paymentMethod;
    private String voucherCode;

    private String customerPhone;
    private String customerName;
    private Integer redeemPoints;

    public String getCustomerPhone() {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public void setRedeemPoints(Integer redeemPoints) {
        this.redeemPoints = redeemPoints;
    }
    public String getCustomerName() {
        return customerName;
    }
    public Integer getRedeemPoints() {
        return redeemPoints;
    }
    public Long getOrderId(){return orderId;} public void setOrderId(Long v){this.orderId=v;}
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){this.paymentMethod=v;}
    public String getVoucherCode(){return voucherCode;} public void setVoucherCode(String v){this.voucherCode=v;}
}
