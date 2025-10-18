package com.example.restaurant.dto.inventory.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecipeItemRequest {
    @NotNull
    private Long ingredientId;

    @NotNull @Positive
    private Double quantity;

    public Long getIngredientId(){ return ingredientId; }
    public void setIngredientId(Long ingredientId){ this.ingredientId = ingredientId; }
    public Double getQuantity(){ return quantity; }
    public void setQuantity(Double quantity){ this.quantity = quantity; }
}
