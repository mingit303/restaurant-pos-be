    package com.example.restaurant.domain.order;

    import com.example.restaurant.domain.employee.Employee;
    import com.example.restaurant.domain.menu.MenuItem;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

    import jakarta.persistence.*;
    import lombok.*;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    @Entity
    @Table(name = "order_items")
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class OrderItem {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id")
        private Order order;

        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "menu_item_id")
        private MenuItem menuItem;

        @Column(nullable = false)
        private BigDecimal unitPrice;

        @Column(nullable = false)
        private Integer quantity;

        @Column(nullable = false)
        private BigDecimal lineTotal;

        private String note;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private OrderItemState state = OrderItemState.PENDING;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "chef_id")
        private Employee chef;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        /** Thời điểm món nấu xong (khi state -> DONE) */
        @Column(name = "done_at")
        private LocalDateTime doneAt;

    }
