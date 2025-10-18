package com.example.restaurant.service.inventory;

import com.example.restaurant.domain.inventory.Ingredient;
// import com.example.restaurant.dto.inventory.*;
import com.example.restaurant.dto.inventory.request.IngredientRequest;
import com.example.restaurant.dto.inventory.response.IngredientResponse;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.inventory.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepo;

    @Transactional(readOnly = true)
    public Page<IngredientResponse> list(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Ingredient> p = (keyword==null || keyword.isBlank())
                ? ingredientRepo.findAll(pageable)
                : ingredientRepo.findByNameContainingIgnoreCase(keyword, pageable);
        return p.map(i -> new IngredientResponse(i.getId(), i.getName(), i.getUnit(), i.getStockQuantity()));
    }

    @Transactional
    public IngredientResponse create(IngredientRequest req) {
        Ingredient i = Ingredient.builder()
                .name(req.getName())
                .unit(req.getUnit())
                .stockQuantity(req.getStockQuantity() != null ? req.getStockQuantity() : 0.0)
                .build();
        ingredientRepo.save(i);
        return new IngredientResponse(i.getId(), i.getName(), i.getUnit(), i.getStockQuantity());
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientRequest req) {
        Ingredient i = ingredientRepo.findById(id).orElseThrow(() -> new NotFoundException("Ingredient not found"));
        i.setName(req.getName());
        i.setUnit(req.getUnit());
        if (req.getStockQuantity()!=null) i.setStockQuantity(req.getStockQuantity());
        ingredientRepo.save(i);
        return new IngredientResponse(i.getId(), i.getName(), i.getUnit(), i.getStockQuantity());
    }

    @Transactional
    public void delete(Long id) {
        Ingredient i = ingredientRepo.findById(id).orElseThrow(() -> new NotFoundException("Ingredient not found"));
        ingredientRepo.delete(i);
    }
}
