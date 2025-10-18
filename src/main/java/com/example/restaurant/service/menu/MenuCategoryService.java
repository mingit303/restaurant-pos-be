package com.example.restaurant.service.menu;

import com.example.restaurant.domain.menu.MenuCategory;
import com.example.restaurant.repository.menu.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryRepository repo;

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
        c.setName(updated.getName());
        return repo.save(c);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
