package com.example.restaurant.domain.invoice;

import com.example.restaurant.domain.order.Order;
import com.example.restaurant.domain.voucher.Voucher;
import com.example.restaurant.domain.customer.Customer;
import com.example.restaurant.domain.employee.Employee;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private BigDecimal subtotal;
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal vatRate = BigDecimal.valueOf(0.10); // 10%

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal vatAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Builder.Default
    private Integer redeemedPoints = 0;
    
    @Builder.Default
    private BigDecimal redeemedValue = BigDecimal.ZERO;

    private String transactionNo;   // Mã giao dịch từ VNPAY
    private String transactionRef;  // Ref unique khi redirect
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    @ManyToOne
    @JoinColumn(name = "cashier_id", nullable = true)
    private Employee cashier;
}
