package com.example.restaurant.domain.inventory;

import jakarta.persistence.*;
import lombok.*;
// import java.util.*;

@Entity
@Table(name = "ingredients")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ingredient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique=true) 
    private String name;

    private String unit;

    @Builder.Default 
    private Double stockQuantity = 0.0;
    // @Builder.Default private double threshold = 0;

    // @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
}
