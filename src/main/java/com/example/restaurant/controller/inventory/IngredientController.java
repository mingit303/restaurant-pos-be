package com.example.restaurant.controller.inventory;

// import com.example.restaurant.dto.inventory.*;
import com.example.restaurant.dto.inventory.request.IngredientRequest;
import com.example.restaurant.dto.inventory.response.IngredientResponse;
import com.example.restaurant.service.inventory.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/ingredients") @RequiredArgsConstructor
public class IngredientController {
    private final IngredientService svc;

    @GetMapping
    public ResponseEntity<Page<IngredientResponse>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(required=false) String keyword
    ){
        return ResponseEntity.ok(svc.list(page,size,keyword));
    }

    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest req){
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientRequest req){
        return ResponseEntity.ok(svc.update(id,req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        svc.delete(id); return ResponseEntity.noContent().build();
    }
}
