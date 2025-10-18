// UpdateCategoryRequest.java
package com.example.restaurant.dto.menu;
import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(@NotBlank String name, Integer sortOrder, Boolean active) {}
