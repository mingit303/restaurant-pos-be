package com.example.restaurant.repository.inventory;

import com.example.restaurant.domain.inventory.RecipeIngredient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    boolean existsByIngredient_Id(Long ingredientId);

    List<RecipeIngredient> findByIngredient_Id(Long ingredientId);
}
