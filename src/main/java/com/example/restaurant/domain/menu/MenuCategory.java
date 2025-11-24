package com.example.restaurant.domain.menu;

import jakarta.persistence.*;
import lombok.*;
// import java.util.*;

@Entity
@Table(name = "menu_categories")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}