package com.example.restaurant.dto.menu.response;

import java.math.BigDecimal;
import java.util.List;

public class MenuItemResponse {
    private Long id; private String name; private String description;
    private BigDecimal price; private String imageUrl; private String categoryName;
    private List<RecipeIngredientDto> recipe;

    public static class RecipeIngredientDto {
        private Long ingredientId; private String ingredientName; private String unit; private Double quantity;
        public RecipeIngredientDto(){}
        public RecipeIngredientDto(Long id,String name,String unit,Double q){
            this.ingredientId=id;this.ingredientName=name;this.unit=unit;this.quantity=q;
        }
        public Long getIngredientId(){return ingredientId;} public void setIngredientId(Long v){this.ingredientId=v;}
        public String getIngredientName(){return ingredientName;} public void setIngredientName(String v){this.ingredientName=v;}
        public String getUnit(){return unit;} public void setUnit(String v){this.unit=v;}
        public Double getQuantity(){return quantity;} public void setQuantity(Double v){this.quantity=v;}
    }

    public MenuItemResponse(){}
    public MenuItemResponse(Long id,String name,String desc,BigDecimal price,String img,String cat,List<RecipeIngredientDto> r){
        this.id=id;this.name=name;this.description=desc;this.price=price;this.imageUrl=img;this.categoryName=cat;this.recipe=r;
    }
    public MenuItemResponse(Long id, String name, String desc,
                        BigDecimal price, String img, String cat) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.imageUrl = img;
        this.categoryName = cat;
        this.recipe = null;
    }
    // getters/setters
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public String getName(){return name;} public void setName(String v){this.name=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public java.math.BigDecimal getPrice(){return price;} public void setPrice(java.math.BigDecimal v){this.price=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){this.imageUrl=v;}
    public String getCategoryName(){return categoryName;} public void setCategoryName(String v){this.categoryName=v;}
    public List<RecipeIngredientDto> getRecipe(){return recipe;} public void setRecipe(List<RecipeIngredientDto> v){this.recipe=v;}
}
