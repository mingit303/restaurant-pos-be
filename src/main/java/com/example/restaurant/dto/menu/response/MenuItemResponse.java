package com.example.restaurant.dto.menu.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class MenuItemResponse {
    private Long id; 
    private String name;
    private String description;
    private BigDecimal price; 
    private String imageUrl;
    private boolean available;
    private MenuCategoryResponse category;
    private List<RecipeIngredientDto> recipe;
    private boolean canSell;

    // DTO cho nguyên liệu trong công thức món
    @Data
    public static class RecipeIngredientDto {
        private Long ingredientId;
        private String ingredientName;
        private Double stockQuantity;
        private String baseUnit;
        private String useUnit;
        private Double convertRate;
        private Double threshold;
        
        public RecipeIngredientDto() {}

        // Constructor đầy đủ — khớp với MenuMapper
        public RecipeIngredientDto(Long id, String name, Double stockQuantity,
                                   String baseUnit, String useUnit,
                                   Double convertRate, Double threshold) {
            this.ingredientId = id;
            this.ingredientName = name;
            this.stockQuantity = stockQuantity;
            this.baseUnit = baseUnit;
            this.useUnit = useUnit;
            this.convertRate = convertRate;
            this.threshold = threshold;
        }
    }

    // Constructors cho MenuItemResponse
    public MenuItemResponse() {}

    @SuppressWarnings("unchecked")
    public MenuItemResponse(Long id, String name, String desc,
                            BigDecimal price, String img, boolean available,
                            MenuCategoryResponse cat, List<?> r, boolean canSell) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.imageUrl = img;
        this.available = available;
        this.category = cat;
        this.recipe = (List<RecipeIngredientDto>)r;
        this.canSell = canSell;
    }

    public MenuItemResponse(Long id, String name, String desc,
                            BigDecimal price, String img, boolean available,
                            MenuCategoryResponse cat, boolean canSell) {
        this(id, name, desc, price, img, available, cat, null, canSell);
    }
}
