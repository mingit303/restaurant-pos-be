package com.example.restaurant.dto.table.request;

import jakarta.validation.constraints.*;

public class UpdateTableStatusRequest {

    @NotBlank
    private String status;  // FREE / OCCUPIED / CLEANING / OUT_OF_SERVICE

    public UpdateTableStatusRequest() {}

    public UpdateTableStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
