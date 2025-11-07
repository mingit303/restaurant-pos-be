package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.*;

public class IngredientRequest {
    @NotBlank private String name;
    @NotBlank private String baseUnit;
    @NotNull @PositiveOrZero private Double stockQuantity;

    @NotBlank private String useUnit;
    @NotNull @Positive private Double convertRate;

    @NotNull @PositiveOrZero private Double threshold;

    public String getName() {
        return name;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public Double getStockQuantity() {
        return stockQuantity;
    }

    public String getUseUnit() {
        return useUnit;
    }

    public Double getConvertRate() {
        return convertRate;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public void setStockQuantity(Double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setUseUnit(String useUnit) {
        this.useUnit = useUnit;
    }

    public void setConvertRate(Double convertRate) {
        this.convertRate = convertRate;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    
}
