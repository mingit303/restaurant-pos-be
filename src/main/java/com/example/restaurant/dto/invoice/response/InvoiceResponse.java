package com.example.restaurant.dto.invoice.response;

import com.example.restaurant.domain.invoice.InvoiceStatus;
import com.example.restaurant.domain.invoice.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceResponse {
    private Long id;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private InvoiceStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal total;
    private String voucherCode;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String transactionRef;
    private String customerName;
    private String customerPhone;
    public InvoiceResponse(){}
    public InvoiceResponse(Long id, Long orderId, PaymentMethod method, InvoiceStatus status,
                           BigDecimal subtotal, BigDecimal discount,  BigDecimal total,
                           String voucherCode, LocalDateTime createdAt, LocalDateTime paidAt, String ref, String customerName, String customerPhone, BigDecimal vatRate,
                            BigDecimal vatAmount){
        this.id=id;
        this.orderId=orderId;
        this.paymentMethod=method;
        this.status=status;
        this.subtotal=subtotal;
        this.discount=discount;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.total=total;
        this.voucherCode=voucherCode;
        this.createdAt=createdAt;
        this.paidAt=paidAt;
        this.transactionRef=ref;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
    }
    

    public BigDecimal getVatRate() {
        return vatRate;
    }
    public BigDecimal getVatAmount() {
        return vatAmount;
    }
    // getters/setters omitted for brevity
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }
    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }
}