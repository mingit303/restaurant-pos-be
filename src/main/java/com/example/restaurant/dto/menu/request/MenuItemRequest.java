package com.example.restaurant.dto.menu.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
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

    @Data
    public static class RecipeItem {

        @NotNull(message = "Nguyên liệu không được để trống")
        private Long ingredientId;

        // Đơn vị dùng nội bộ, FE tự load từ Ingredient 
        private String unit;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Double quantity;
    }
}
