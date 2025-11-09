package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.NotNull;

public class ChangeTableRequest {
    @NotNull
    private Long newTableId;

    public Long getNewTableId() { return newTableId; }
    public void setNewTableId(Long newTableId) { this.newTableId = newTableId; }
}