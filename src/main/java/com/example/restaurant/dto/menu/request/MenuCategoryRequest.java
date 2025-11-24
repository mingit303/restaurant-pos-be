package com.example.restaurant.dto.menu.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuCategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
}
