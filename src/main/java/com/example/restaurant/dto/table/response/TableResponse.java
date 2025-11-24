package com.example.restaurant.dto.table.response;

import com.example.restaurant.domain.table.TableStatus;

import lombok.Data;

@Data
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
}
