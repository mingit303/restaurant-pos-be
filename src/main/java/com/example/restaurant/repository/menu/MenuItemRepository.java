// repository/menu/MenuItemRepository.java
package com.example.restaurant.repository.menu;
import com.example.restaurant.domain.menu.MenuItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, JpaSpecificationExecutor<MenuItem> {
    Page<MenuItem> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<MenuItem> findByCategoryId(Long categoryId, Pageable pageable);
    Page<MenuItem> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String keyword, Pageable pageable);
}