package com.example.restaurant.repository.inventory;

import com.example.restaurant.domain.inventory.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {}
