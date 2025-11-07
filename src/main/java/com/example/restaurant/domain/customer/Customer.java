package com.example.restaurant.domain.customer;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Builder.Default
    @Column(name = "total_points") 
    private Integer totalPoints = 0;

    @Column(name = "created_at")   
    private LocalDateTime createdAt;

    @Column(name = "updated_at")   
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<PointHistory> histories;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
