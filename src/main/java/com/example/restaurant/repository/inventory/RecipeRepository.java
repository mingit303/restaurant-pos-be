package com.example.restaurant.repository.inventory;

import com.example.restaurant.domain.inventory.Recipe;
import com.example.restaurant.domain.menu.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByMenuItem(MenuItem menuItem);
    Optional<Recipe> findByMenuItemId(Long menuItemId);
}
