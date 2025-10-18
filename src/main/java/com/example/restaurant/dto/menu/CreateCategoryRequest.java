// CreateCategoryRequest.java
package com.example.restaurant.dto.menu;
import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name, Integer sortOrder) {}
