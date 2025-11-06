package com.example.restaurant.dto.customer;

import java.time.LocalDateTime;

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
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public Integer getTotalPoints() {
        return totalPoints;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

