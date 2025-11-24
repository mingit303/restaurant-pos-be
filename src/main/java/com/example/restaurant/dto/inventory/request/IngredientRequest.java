package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class IngredientRequest {
    @NotBlank private String name;
    @NotBlank private String baseUnit;
    @NotNull @PositiveOrZero private Double stockQuantity;

    @NotBlank private String useUnit;
    @NotNull @Positive private Double convertRate;

    @NotNull @PositiveOrZero private Double threshold;
 
}
