package com.example.restaurant.dto.menu.response;

import java.math.BigDecimal;
import java.util.List;

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

        // Getters & Setters
        public Long getIngredientId() { return ingredientId; }
        public void setIngredientId(Long v) { this.ingredientId = v; }

        public String getIngredientName() { return ingredientName; }
        public void setIngredientName(String v) { this.ingredientName = v; }

        public Double getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Double v) { this.stockQuantity = v; }

        public String getBaseUnit() { return baseUnit; }
        public void setBaseUnit(String v) { this.baseUnit = v; }

        public String getUseUnit() { return useUnit; }
        public void setUseUnit(String v) { this.useUnit = v; }

        public Double getConvertRate() { return convertRate; }
        public void setConvertRate(Double v) { this.convertRate = v; }

        public Double getThreshold() { return threshold; }
        public void setThreshold(Double v) { this.threshold = v; }
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

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public MenuCategoryResponse getCategory() { return category; }
    public void setCategory(MenuCategoryResponse category) { this.category = category; }

    public List<RecipeIngredientDto> getRecipe() { return recipe; }
    public void setRecipe(List<RecipeIngredientDto> recipe) { this.recipe = recipe; }
    public boolean isCanSell() { return canSell; }
    public void setCanSell(boolean canSell) { this.canSell = canSell; }
}
