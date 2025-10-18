package com.example.restaurant.dto.inventory.response;

public class IngredientResponse {
    private Long id; private String name; private String unit; private double stockQuantity; 
    // private double threshold;
    public IngredientResponse(){}
    public IngredientResponse(Long id, String name, String unit, double stockQuantity){
        this.id=id; this.name=name; this.unit=unit; this.stockQuantity=stockQuantity; 
        // this.threshold=threshold;
    }
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public String getName(){ return name; } public void setName(String name){ this.name=name; }
    public String getUnit(){ return unit; } public void setUnit(String unit){ this.unit=unit; }
    public double getStockQuantity(){ return stockQuantity; } public void setStockQuantity(double q){ this.stockQuantity=q; }
    // public double getThreshold(){ return threshold; } public void setThreshold(double t){ this.threshold=t; }
}
