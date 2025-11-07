package com.example.restaurant.mapper;

import com.example.restaurant.domain.menu.MenuItem;
import com.example.restaurant.dto.menu.response.*;

import java.util.stream.Collectors;

public class MenuMapper {

    public static MenuItemResponse toResponse(MenuItem m) {
        if (m == null) return null;

        return new MenuItemResponse(
            m.getId(),
            m.getName(),
            m.getDescription(),
            m.getPrice(),
            m.getImageUrl(),
            m.isAvailable(),
            m.getCategory() != null
                ? new MenuCategoryResponse(m.getCategory().getId(), m.getCategory().getName())
                : null,
            null  // chưa cần map recipe
        );
    }

    public static MenuItemResponse toResponseWithRecipe(MenuItem m) {
        if (m == null) return null;

        var recipe = (m.getRecipe() != null && m.getRecipe().getIngredients() != null)
                ? m.getRecipe().getIngredients().stream().map(ri ->
                    new MenuItemResponse.RecipeIngredientDto(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getIngredient().getStockQuantity(),
                        ri.getIngredient().getBaseUnit(),
                        ri.getIngredient().getUseUnit(),
                        ri.getIngredient().getConvertRate(),
                        ri.getIngredient().getThreshold()
                    )
                ).collect(Collectors.toList())
                : null;

        return new MenuItemResponse(
            m.getId(),
            m.getName(),
            m.getDescription(),
            m.getPrice(),
            m.getImageUrl(),
            m.isAvailable(),
            m.getCategory() != null
                ? new MenuCategoryResponse(m.getCategory().getId(), m.getCategory().getName())
                : null,
            recipe
        );
    }
}
