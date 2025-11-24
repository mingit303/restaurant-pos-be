package com.example.restaurant.dto.menu.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class MenuItemRequest {

    @NotBlank(message = "Tên món không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    private String imageUrl;

    @NotNull(message = "Loại món không được để trống")
    private Long categoryId;

    /**
     * recipeItems KHÔNG BẮT BUỘC.
     * Nếu gửi rỗng → xóa công thức cũ (MenuService đã xử lý).
     * Nếu gửi danh sách → validate từng item.
     */
    @Valid
    private List<RecipeItem> recipeItems;


    // -------------------- RecipeItem --------------------
    public static class RecipeItem {

        /** Chỉ cần ingredientId và quantity, không cần name/unit */
        @NotNull(message = "Nguyên liệu không được để trống")
        private Long ingredientId;

        /** Đơn vị dùng nội bộ → FE tự load từ Ingredient */
        private String unit;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Double quantity;

        // Getters - Setters
        public Long getIngredientId() { return ingredientId; }
        public void setIngredientId(Long v) { this.ingredientId = v; }

        public String getUnit() { return unit; }
        public void setUnit(String v) { this.unit = v; }

        public Double getQuantity() { return quantity; }
        public void setQuantity(Double v) { this.quantity = v; }
    }


    // -------------------- Getters / Setters --------------------
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { this.price = v; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String v) { this.imageUrl = v; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long v) { this.categoryId = v; }

    public List<RecipeItem> getRecipeItems() { return recipeItems; }
    public void setRecipeItems(List<RecipeItem> v) { this.recipeItems = v; }
}
