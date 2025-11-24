// repository/menu/MenuCategoryRepository.java
package com.example.restaurant.repository.menu;
import com.example.restaurant.domain.menu.MenuCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long>,JpaSpecificationExecutor<MenuCategory>  {
    boolean existsByName(String name);
    Optional<MenuCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}