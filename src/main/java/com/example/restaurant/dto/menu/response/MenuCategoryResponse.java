// dto/menu/MenuCategoryResponse.java
package com.example.restaurant.dto.menu.response;

import lombok.Data;

@Data
public class MenuCategoryResponse {
    private Long id; 
    private String name;
    public MenuCategoryResponse() {}
    public MenuCategoryResponse(Long id,String name){
        this.id = id;
        this.name = name;
    }
}
