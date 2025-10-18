package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.*;

public class IngredientRequest {
    @NotBlank private String name;
    @NotBlank private String unit;
    @NotNull @PositiveOrZero private double stockQuantity;
    // @NotNull @PositiveOrZero private double threshold;

    public String getName(){ return name; } public void setName(String n){ this.name=n; }
    public String getUnit(){ return unit; } public void setUnit(String u){ this.unit=u; }
    public Double getStockQuantity(){ return stockQuantity; } public void setStockQuantity(Double q){ this.stockQuantity=q; }
    // public double getThreshold(){ return threshold; } public void setThreshold(double t){ this.threshold=t; }
}