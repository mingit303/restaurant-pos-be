package com.example.restaurant.domain.menu;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.example.restaurant.domain.inventory.Recipe;

@Entity
@Table(name = "menu_items")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    @Builder.Default
    private boolean available = true; 
    
    @OneToOne(mappedBy="menuItem", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private Recipe recipe;
}
