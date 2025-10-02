package com.example.restaurant.dto.table;                  // 1

import jakarta.validation.constraints.*;                   // 2

public record UpdateTableStatusRequest(                    // 3
        @NotBlank String status                            // 4: FREE/RESERVED/OCCUPIED/CLEANING/OUT_OF_SERVICE
) {}
