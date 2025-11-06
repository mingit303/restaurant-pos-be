package com.example.restaurant.controller.inventory;

// import com.example.restaurant.dto.inventory.response.RecipeDetailResponse;
import com.example.restaurant.service.inventory.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/recipes") @RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/by-menu/{menuItemId}")
    public ResponseEntity<com.example.restaurant.dto.inventory.response.RecipeDetailResponse> getByMenu(@PathVariable Long menuItemId){
        return ResponseEntity.ok(recipeService.getDetailByMenuItemId(menuItemId));
    }
}
