package com.example.restaurant.dto.table.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateTableStatusRequest {

    @NotBlank
    private String status;  // FREE / OCCUPIED / CLEANING / OUT_OF_SERVICE

    public UpdateTableStatusRequest() {}

    public UpdateTableStatusRequest(String status) {
        this.status = status;
    }
}
