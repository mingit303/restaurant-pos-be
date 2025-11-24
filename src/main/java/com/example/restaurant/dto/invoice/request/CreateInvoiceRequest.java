package com.example.restaurant.dto.invoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInvoiceRequest {
    @NotNull private Long orderId;
    @NotNull private String paymentMethod;
    private String voucherCode;

    private String customerPhone;
    private String customerName;
    private Integer redeemPoints;
}
