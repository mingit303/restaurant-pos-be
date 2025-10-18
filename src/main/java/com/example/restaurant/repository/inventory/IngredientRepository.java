package com.example.restaurant.repository.inventory;

import com.example.restaurant.domain.inventory.Ingredient;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Page<Ingredient> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<Ingredient> findByNameIgnoreCase(String name);
}
