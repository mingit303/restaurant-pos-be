package com.example.restaurant.service.inventory;

// import com.example.restaurant.domain.inventory.*;
import com.example.restaurant.domain.menu.MenuItem;
import com.example.restaurant.dto.inventory.response.RecipeDetailResponse;
import com.example.restaurant.dto.menu.response.MenuItemResponse;
import com.example.restaurant.mapper.MenuMapper;
import com.example.restaurant.repository.inventory.*;
import com.example.restaurant.repository.menu.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepo;
    // private final RecipeIngredientRepository riRepo; // nếu bạn chưa tạo, có thể bỏ member này
    private final IngredientRepository ingredientRepo;
    private final MenuItemRepository menuRepo;

    // gọi khi OrderItem DONE
    @Transactional(readOnly = true)
    public void consumeFor(MenuItem menuItem) {
        var recipe = recipeRepo.findByMenuItemId(menuItem.getId()).orElseThrow();
        for (var ri : recipe.getIngredients()) {
            var ing = ri.getIngredient();

            // 🔁 Quy đổi sang đơn vị nhập
            double usedInBase = ri.getQuantity() / ing.getConvertRate();
            double newQty = ing.getStockQuantity() - usedInBase;

            if (newQty < 0)
                throw new IllegalStateException("Không đủ tồn kho cho " + ing.getName());

            ing.setStockQuantity(newQty);
            ingredientRepo.save(ing);
        }
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getRecipeView(Long menuItemId) {
        var menu = menuRepo.findById(menuItemId).orElseThrow();
        return MenuMapper.toResponseWithRecipe(menu);
    }


    @Transactional(readOnly = true)
    public RecipeDetailResponse getDetailByMenuItemId(Long menuItemId) {
        var recipe = recipeRepo.findByMenuItemId(menuItemId).orElseThrow();
        var items = recipe.getIngredients().stream().map(ri ->
                new RecipeDetailResponse.IngredientView(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getIngredient().getUseUnit(),
                        ri.getQuantity()
                )
        ).collect(Collectors.toList());
        return new RecipeDetailResponse(menuItemId, recipe.getMenuItem().getName(), items);
    }
}
