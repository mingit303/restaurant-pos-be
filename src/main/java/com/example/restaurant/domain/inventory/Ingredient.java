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

    @Builder.Default 
    private Double stockQuantity = 0.0;

    private String baseUnit;

    private String useUnit;

    private double convertRate;

    private double threshold;
}
