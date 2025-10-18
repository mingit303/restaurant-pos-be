package com.example.restaurant.dto.menu.request;

import jakarta.validation.constraints.NotBlank;

public class MenuCategoryRequest {
    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
}
