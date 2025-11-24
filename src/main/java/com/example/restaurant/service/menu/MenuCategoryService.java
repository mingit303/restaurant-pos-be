package com.example.restaurant.service.menu;

import com.example.restaurant.domain.menu.MenuCategory;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.menu.MenuCategoryRepository;
import com.example.restaurant.repository.menu.MenuItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryRepository repo;
    private final MenuItemRepository menuItemRepo;

    public Page<MenuCategory> getAll(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.isBlank())
            return repo.findAll((root, q, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"), pageable);
        return repo.findAll(pageable);
    }

    public MenuCategory create(MenuCategory cat) {
        if (repo.findByNameIgnoreCase(cat.getName()).isPresent())
            throw new IllegalStateException("Danh mục đã tồn tại!");
        return repo.save(cat);
    }

    public MenuCategory update(Long id, MenuCategory updated) {
        MenuCategory c = repo.findById(id).orElseThrow(() -> new IllegalStateException("Không tìm thấy danh mục"));
        if (repo.findByNameIgnoreCase(updated.getName()).isPresent())
            throw new IllegalStateException("Danh mục đã tồn tại!");
        c.setName(updated.getName());
        return repo.save(c);
    }

    @Transactional
    public void delete(Long id) {
        MenuCategory c = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục."));

        if (menuItemRepo.existsByCategory_Id(id)) {
            throw new BadRequestException("Không thể xóa. Danh mục vẫn còn món ăn.");
        }

        repo.delete(c);
    }

}
