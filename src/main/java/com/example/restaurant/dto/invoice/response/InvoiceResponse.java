package com.example.restaurant.dto.invoice.response;

import com.example.restaurant.domain.invoice.InvoiceStatus;
import com.example.restaurant.domain.invoice.PaymentMethod;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
    private String tableCode;
    private BigDecimal afterDiscount;
    public InvoiceResponse(){}
    public InvoiceResponse(Long id, Long orderId, PaymentMethod method, InvoiceStatus status,
                           BigDecimal subtotal, BigDecimal discount,  BigDecimal total,
                           String voucherCode, LocalDateTime createdAt, LocalDateTime paidAt, String ref, String customerName, String customerPhone, BigDecimal vatRate,
                            BigDecimal vatAmount, String tableCode, BigDecimal afterDiscount){
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = method;
        this.status = status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.total = total;
        this.voucherCode = voucherCode;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.transactionRef = ref;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.tableCode = tableCode;
        this.afterDiscount = afterDiscount;
    }
}