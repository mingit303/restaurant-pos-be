package com.example.restaurant.dto.inventory.response;

import java.util.List;

public class RecipeDetailResponse {
    private Long menuItemId;
    private String menuName;
    private List<IngredientView> ingredients;

    public static class IngredientView {
        private Long id;
        private String name;
        private String unit;
        private Double quantity;

        public IngredientView() {}
        public IngredientView(Long id, String name, String unit, Double quantity) {
            this.id = id; this.name = name; this.unit = unit; this.quantity = quantity;
        }
        public Long getId(){return id;} public void setId(Long v){this.id=v;}
        public String getName(){return name;} public void setName(String v){this.name=v;}
        public String getUnit(){return unit;} public void setUnit(String v){this.unit=v;}
        public Double getQuantity(){return quantity;} public void setQuantity(Double v){this.quantity=v;}
    }

    public RecipeDetailResponse() {}
    public RecipeDetailResponse(Long menuItemId, String menuName, List<IngredientView> ingredients) {
        this.menuItemId = menuItemId; this.menuName = menuName; this.ingredients = ingredients;
    }
    public Long getMenuItemId(){return menuItemId;} public void setMenuItemId(Long v){this.menuItemId=v;}
    public String getMenuName(){return menuName;} public void setMenuName(String v){this.menuName=v;}
    public List<IngredientView> getIngredients(){return ingredients;} public void setIngredients(List<IngredientView> v){this.ingredients=v;}
}
