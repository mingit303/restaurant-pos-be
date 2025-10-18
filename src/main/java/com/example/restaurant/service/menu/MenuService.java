package com.example.restaurant.service.menu;

import com.example.restaurant.domain.inventory.Ingredient;
import com.example.restaurant.domain.menu.*;
import com.example.restaurant.domain.inventory.Recipe;
import com.example.restaurant.domain.inventory.RecipeIngredient;
import com.example.restaurant.dto.menu.request.MenuItemRequest;
// import com.example.restaurant.dto.inventory.request.RecipeItemRequest;
import com.example.restaurant.dto.menu.response.MenuItemResponse;
import com.example.restaurant.repository.inventory.IngredientRepository;
import com.example.restaurant.repository.inventory.RecipeIngredientRepository;
import com.example.restaurant.repository.inventory.RecipeRepository;
import org.springframework.data.jpa.domain.Specification;
import com.example.restaurant.repository.menu.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;

@Service @RequiredArgsConstructor
public class MenuService {
    private final MenuItemRepository menuRepo;
    private final MenuCategoryRepository catRepo;
    private final IngredientRepository ingRepo;
    private final RecipeRepository recipeRepo;
    private final RecipeIngredientRepository riRepo;
    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads", "images", "menu");
    @Transactional(readOnly = true)
    public Page<MenuItemResponse> list(int page,int size,String keyword, Long categoryId){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Specification<MenuItem> spec = (root, q, cb) -> cb.conjunction();
        if (keyword!=null && !keyword.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), "%"+keyword.toLowerCase()+"%"));
        }
        if (categoryId!=null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        return menuRepo.findAll(spec, pageable).map(m ->
            new MenuItemResponse(
                m.getId(), m.getName(), m.getDescription(), m.getPrice(),
                m.getImageUrl(), m.getCategory()!=null?m.getCategory().getName():null
            )
        );
    }

    @Transactional
    public MenuItemResponse create(MenuItemRequest req){
        MenuCategory cat = catRepo.findById(req.getCategoryId()).orElseThrow();
        MenuItem m = MenuItem.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .imageUrl(req.getImageUrl())
                .category(cat)
                .build();
        menuRepo.save(m);

        upsertRecipe(m, req);
        return toRes(m);
    }

    @Transactional
    public MenuItemResponse update(Long id, MenuItemRequest req){
        MenuItem m = menuRepo.findById(id).orElseThrow();
        MenuCategory cat = catRepo.findById(req.getCategoryId()).orElseThrow();

        // xóa ảnh cũ nếu thay ảnh
        if (req.getImageUrl()!=null && !req.getImageUrl().equals(m.getImageUrl()))
            deleteOldImage(m.getImageUrl());

        m.setName(req.getName());
        m.setDescription(req.getDescription());
        m.setPrice(req.getPrice());
        m.setImageUrl(req.getImageUrl());
        m.setCategory(cat);
        menuRepo.save(m);

        upsertRecipe(m, req);
        return toRes(m);
    }

    @Transactional
    public void delete(Long id){
        MenuItem m = menuRepo.findById(id).orElseThrow();
        deleteOldImage(m.getImageUrl());
        // xóa recipe
        recipeRepo.findByMenuItem(m).ifPresent(r -> {
            riRepo.deleteAll(r.getIngredients());
            recipeRepo.delete(r);
        });
        menuRepo.delete(m);
    }

    // ----- helpers -----
    private void upsertRecipe(MenuItem m, MenuItemRequest req) {
        Recipe recipe = recipeRepo.findByMenuItem(m).orElseGet(() -> Recipe.builder().menuItem(m).build());
        // clear cũ
        riRepo.deleteAll(recipe.getIngredients());
        recipe.getIngredients().clear();
        recipeRepo.save(recipe);

        for (MenuItemRequest.RecipeItem line : req.getRecipeItems()) {
            Ingredient ing = ingRepo.findById(line.getIngredientId()).orElseThrow();
            RecipeIngredient ri = RecipeIngredient.builder()
                    .recipe(recipe)
                    .ingredient(ing)
                    .quantity(line.getQuantity())
                    .build();
            riRepo.save(ri);
            recipe.getIngredients().add(ri);
        }
        recipeRepo.save(recipe);
    }

    private void deleteOldImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/images/menu/")) return;

        String fileName = Paths.get(imageUrl).getFileName().toString();
        Path file = UPLOAD_DIR.resolve(fileName);
        System.out.println("🧭 Deleting: " + file.toAbsolutePath());

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("⚠️ Could not delete: " + e.getMessage());
        }
    }

    private MenuItemResponse toRes(MenuItem m){
        return new MenuItemResponse(
            m.getId(), m.getName(), m.getDescription(), m.getPrice(),
            m.getImageUrl(), m.getCategory()!=null?m.getCategory().getName():null
        );
    }   
}
