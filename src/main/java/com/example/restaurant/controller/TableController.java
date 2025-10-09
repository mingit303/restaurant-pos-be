package com.example.restaurant.controller;

import com.example.restaurant.dto.table.*;
import com.example.restaurant.service.TableService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<TableResponse> getAll() {
        return tableService.getAll();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')")
    public TableResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody UpdateTableStatusRequest body) {
        return tableService.updateStatus(id, body.status());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TableResponse create(@Valid @RequestBody CreateTableRequest req) {
        return tableService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TableResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTableRequest req) {
        return tableService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}