package com.example.restaurant.controller.table;

// import com.example.restaurant.dto.table.*;
import com.example.restaurant.dto.table.request.CreateTableRequest;
import com.example.restaurant.dto.table.request.UpdateTableRequest;
import com.example.restaurant.dto.table.request.UpdateTableStatusRequest;
import com.example.restaurant.dto.table.response.TableResponse;
import com.example.restaurant.service.table.TableService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<Page<TableResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(tableService.list(page, size, keyword, status));
    }


    //                                                                                                                                                                                       
    @PostMapping @PreAuthorize("hasAuthority('ROLE_ADMIN')")public ResponseEntity<TableResponse> create(@Valid @RequestBody CreateTableRequest req){ return ResponseEntity.ok(tableService.create(req)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<TableResponse> update(@PathVariable Long id,@Valid @RequestBody UpdateTableRequest req){ return ResponseEntity.ok(tableService.update(id, req)); }
    @PatchMapping("/{id}/status")@PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')") public ResponseEntity<TableResponse> updateStatus(@PathVariable Long id,@Valid @RequestBody UpdateTableStatusRequest req){ return ResponseEntity.ok(tableService.updateStatus(id, req.getStatus())); }
    @DeleteMapping("/{id}")@PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<Void> delete(@PathVariable Long id){ tableService.delete(id); return ResponseEntity.noContent().build(); }
}