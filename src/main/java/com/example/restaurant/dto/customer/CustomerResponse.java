package com.example.restaurant.dto.customer;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String phone;
    private Integer totalPoints;
    private LocalDateTime createdAt;
    
    public CustomerResponse(Long id, String name, String phone, Integer totalPoints, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.totalPoints = totalPoints;
        this.createdAt = createdAt;
    }
}

