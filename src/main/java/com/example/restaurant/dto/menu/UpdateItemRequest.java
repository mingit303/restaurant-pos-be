// UpdateItemRequest.java
package com.example.restaurant.dto.menu;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateItemRequest(
  @NotNull Long categoryId,
  @NotBlank String name,
  @DecimalMin("0.01") BigDecimal price,
  String unitName, String imageUrl, Boolean available
) {}
