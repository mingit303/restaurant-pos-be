package com.example.restaurant.domain.inventory;

import com.example.restaurant.domain.menu.MenuItem;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "recipes")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Recipe {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", unique = true)
    private MenuItem menuItem;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}
