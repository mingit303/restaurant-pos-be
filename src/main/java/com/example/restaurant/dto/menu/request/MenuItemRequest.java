package com.example.restaurant.dto.menu.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class MenuItemRequest {
    @NotBlank private String name;
    private String description;
    @NotNull @Positive private BigDecimal price;
    private String imageUrl;
    @NotNull private Long categoryId;

    private List<RecipeItem> recipeItems;

    public static class RecipeItem {
        private Long ingredientId;
        private String ingredientName; // required if ingredientId == null
        private String unit;           // required if ingredientId == null
        @NotNull @Positive private Double quantity;

        public Long getIngredientId(){return ingredientId;} public void setIngredientId(Long v){this.ingredientId=v;}
        public String getIngredientName(){return ingredientName;} public void setIngredientName(String v){this.ingredientName=v;}
        public String getUnit(){return unit;} public void setUnit(String v){this.unit=v;}
        public Double getQuantity(){return quantity;} public void setQuantity(Double v){this.quantity=v;}
    }

    // getters/setters
    public String getName(){return name;} public void setName(String v){this.name=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public java.math.BigDecimal getPrice(){return price;} public void setPrice(java.math.BigDecimal v){this.price=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){this.imageUrl=v;}
    public Long getCategoryId(){return categoryId;} public void setCategoryId(Long v){this.categoryId=v;}
    public List<RecipeItem> getRecipeItems(){return recipeItems;} public void setRecipeItems(List<RecipeItem> v){this.recipeItems=v;}
}
