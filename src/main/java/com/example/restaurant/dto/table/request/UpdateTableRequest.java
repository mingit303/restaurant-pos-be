package com.example.restaurant.dto.table.request;

import jakarta.validation.constraints.*;

public class UpdateTableRequest {

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotNull
    @Min(1)
    private Integer capacity;

    public UpdateTableRequest() {}

    public UpdateTableRequest(String code, Integer capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
