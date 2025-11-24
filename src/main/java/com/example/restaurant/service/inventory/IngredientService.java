package com.example.restaurant.service.inventory;

import com.example.restaurant.domain.inventory.Ingredient;
import com.example.restaurant.dto.inventory.request.IngredientRequest;
import com.example.restaurant.dto.inventory.request.StockInRequest;
import com.example.restaurant.dto.inventory.response.IngredientResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.inventory.IngredientRepository;
import com.example.restaurant.repository.inventory.RecipeIngredientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepo;
    private final RecipeIngredientRepository recipeIngredientRepo;

    @Transactional(readOnly = true)
    public Page<IngredientResponse> list(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Ingredient> p = (keyword == null || keyword.isBlank())
                ? ingredientRepo.findAll(pageable)
                : ingredientRepo.findByNameContainingIgnoreCase(keyword, pageable);

        return p.map(i -> new IngredientResponse(
                i.getId(),
                i.getName(),
                i.getStockQuantity(),
                i.getBaseUnit(),
                i.getUseUnit(),
                i.getConvertRate(),
                i.getThreshold()
        ));
    }


    @Transactional
    public IngredientResponse create(IngredientRequest req) {

        // Check duplicate
        if (ingredientRepo.existsByNameIgnoreCase(req.getName()))
            throw new RuntimeException("Tên nguyên liệu đã tồn tại!");

        Ingredient i = Ingredient.builder()
                .name(req.getName())
                .baseUnit(req.getBaseUnit())
                .stockQuantity(req.getStockQuantity())
                .useUnit(req.getUseUnit())
                .convertRate(req.getConvertRate())
                .threshold(req.getThreshold())
                .build();

        ingredientRepo.save(i);

        return new IngredientResponse(
                i.getId(),
                i.getName(),
                i.getStockQuantity(),
                i.getBaseUnit(),
                i.getUseUnit(),
                i.getConvertRate(),
                i.getThreshold()
        );
    }


        @Transactional
        public IngredientResponse update(Long id, IngredientRequest req) {

                Ingredient i = ingredientRepo.findById(id)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy nguyên liệu!"));

                // Check duplicate except current ID
                if (ingredientRepo.existsByNameIgnoreCaseAndIdNot(req.getName(), id))
                throw new RuntimeException("Tên nguyên liệu đã tồn tại!");

                i.setName(req.getName());
                i.setBaseUnit(req.getBaseUnit());
                i.setStockQuantity(req.getStockQuantity());
                i.setUseUnit(req.getUseUnit());
                i.setConvertRate(req.getConvertRate());
                i.setThreshold(req.getThreshold());

                ingredientRepo.save(i);

                return new IngredientResponse(
                        i.getId(),
                        i.getName(),
                        i.getStockQuantity(),
                        i.getBaseUnit(),
                        i.getUseUnit(),
                        i.getConvertRate(),
                        i.getThreshold()
                );
        }


        @Transactional
        public void delete(Long id) {

                Ingredient ing = ingredientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nguyên liệu."));

                //Check công thức
                if (recipeIngredientRepo.existsByIngredient_Id(id)) {
                        
                        throw new BadRequestException("Không thể xóa. Nguyên liệu đang dùng trong các món");
                }

                ingredientRepo.delete(ing);
        }

        @Transactional
        public IngredientResponse importStock(Long id, StockInRequest req) {

        Ingredient ing = ingredientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nguyên liệu!"));

        // Tăng tồn kho
        ing.setStockQuantity( ing.getStockQuantity() + req.getAmount() );

        ingredientRepo.save(ing);


                return new IngredientResponse(
                        ing.getId(),
                        ing.getName(),
                        ing.getStockQuantity(),
                        ing.getBaseUnit(),
                        ing.getUseUnit(),
                        ing.getConvertRate(),
                        ing.getThreshold()
                );
        }


}
