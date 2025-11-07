package com.example.restaurant.dto.inventory.response;

public class IngredientResponse {
    private Long id;
    private String name;
    private String baseUnit;
    private double stockQuantity;
    private String useUnit;
    private double convertRate;
    private double threshold;

    private boolean lowStock; // ✅ auto flag

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public String getUseUnit() {
        return useUnit;
    }

    public double getConvertRate() {
        return convertRate;
    }

    public double getThreshold() {
        return threshold;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setUseUnit(String useUnit) {
        this.useUnit = useUnit;
    }

    public void setConvertRate(double convertRate) {
        this.convertRate = convertRate;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    
}

