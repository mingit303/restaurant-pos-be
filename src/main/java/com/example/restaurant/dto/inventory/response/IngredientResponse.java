    package com.example.restaurant.dto.inventory.response;

import lombok.Data;

@Data
public class IngredientResponse {
    private Long id;
    private String name;
    private String baseUnit;
    private double stockQuantity;
    private String useUnit;
    private double convertRate;
    private double threshold;

    private boolean lowStock;

    public IngredientResponse(Long id, String name, 
                            double stockQuantity, String baseUnit, String useUnit, 
                            double convertRate, double threshold) {
        this.id = id;
        this.name = name;
        this.baseUnit = baseUnit;
        this.stockQuantity = stockQuantity;
        this.useUnit = useUnit;
        this.convertRate = convertRate;
        this.threshold = threshold;
        this.lowStock = stockQuantity <= threshold;
    }
}