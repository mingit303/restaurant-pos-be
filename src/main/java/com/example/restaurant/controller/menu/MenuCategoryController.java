package com.example.restaurant.controller.menu;

import com.example.restaurant.domain.menu.MenuCategory;
// import com.example.restaurant.dto.menu.request.MenuCategoryRequest;
import com.example.restaurant.dto.menu.response.MenuCategoryResponse;
// import com.example.restaurant.repository.menu.MenuCategoryRepository;
import com.example.restaurant.service.menu.MenuCategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// import jakarta.validation.Valid;
// import java.util.List;

@RestController
@RequestMapping("/menu/categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService service;

    @GetMapping
    public ResponseEntity<Page<MenuCategoryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MenuCategoryResponse> result = service.getAll(pageable, keyword)
            .map(c -> new MenuCategoryResponse(c.getId(), c.getName())); // chuyển entity -> DTO
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<MenuCategory> create(@RequestBody MenuCategory req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuCategory> update(@PathVariable Long id, @RequestBody MenuCategory req) {
        return ResponseEntity.ok(service.update(id, req));
    }

     @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
