package com.example.restaurant.dto.table.response;

import com.example.restaurant.domain.table.TableStatus;

public class TableResponse {

    private Long id;
    private String code;
    private Integer capacity;
    private TableStatus status;

    public TableResponse() {}

    public TableResponse(Long id, String code, Integer capacity, TableStatus status) {
        this.id = id;
        this.code = code;
        this.capacity = capacity;
        this.status = status;
    }
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }
}
