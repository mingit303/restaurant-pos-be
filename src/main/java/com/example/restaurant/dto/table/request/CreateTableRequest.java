package com.example.restaurant.dto.table.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTableRequest {

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotNull
    @Min(1)
    private Integer capacity;

    public CreateTableRequest() {}

    public CreateTableRequest(String code, Integer capacity) {
        this.code = code;
        this.capacity = capacity;
    }

}
