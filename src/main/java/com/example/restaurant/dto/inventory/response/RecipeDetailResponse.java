package com.example.restaurant.dto.inventory.response;

import java.util.List;

import lombok.Data;

@Data
public class RecipeDetailResponse {
    private Long menuItemId;
    private String menuName;
    private List<IngredientView> ingredients;

    @Data
    public static class IngredientView {
        private Long id;
        private String name;
        private String unit;
        private Double quantity;

        public IngredientView() {}
        public IngredientView(Long id, String name, String unit, Double quantity) {
            this.id = id; 
            this.name = name; 
            this.unit = unit; 
            this.quantity = quantity;
        }
    }

    public RecipeDetailResponse() {}
    public RecipeDetailResponse(Long menuItemId, String menuName, List<IngredientView> ingredients) {
        this.menuItemId = menuItemId; 
        this.menuName = menuName; 
        this.ingredients = ingredients;
    }
}
