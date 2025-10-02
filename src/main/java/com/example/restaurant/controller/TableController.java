package com.example.restaurant.controller;                     // 1

import com.example.restaurant.dto.table.*;                     // 2
import com.example.restaurant.service.*;                       // 3
import jakarta.validation.Valid;                               // 4

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 5
import org.springframework.web.bind.annotation.*;              // 6
import java.util.*;                                            // 7

@RestController                                                 // 8
@RequestMapping("/tables")                                      // 9
public class TableController {

    private final TableService tableService;                    // 10

    public TableController(TableService tableService) {         // 11
        this.tableService = tableService;
    }

    @GetMapping                                                 // 12: GET /tables
    public List<TableResponse> getAll() {                       // 13
        return tableService.getAll();                           // 14
    }

    @PatchMapping("/{id}/status")                               // 15: PATCH /tables/{id}/status
    @PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')")              // 16: Admin và waiter được đổi status bàn
    public TableResponse updateStatus(@PathVariable Long id,    // 17
                                      @Valid @RequestBody UpdateTableStatusRequest body) { // 18: validate input
        return tableService.updateStatus(id, body.status());    // 19
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