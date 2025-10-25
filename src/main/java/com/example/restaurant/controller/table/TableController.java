package com.example.restaurant.controller.table;

// import com.example.restaurant.dto.table.*;
import com.example.restaurant.dto.table.request.CreateTableRequest;
import com.example.restaurant.dto.table.request.UpdateTableRequest;
import com.example.restaurant.dto.table.request.UpdateTableStatusRequest;
import com.example.restaurant.dto.table.response.TableResponse;
import com.example.restaurant.service.table.TableService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    // @GetMapping
    // public List<TableResponse> getAll() {
    //     return tableService.getAll();
    // }

    // @PatchMapping("/{id}/status")
    // @PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')")
    // public TableResponse updateStatus(@PathVariable Long id,
    //                                   @Valid @RequestBody UpdateTableStatusRequest body) {
    //     return tableService.updateStatus(id, body.getStatus());
    // }

    // @PostMapping
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // public TableResponse create(@Valid @RequestBody CreateTableRequest req) {
    //     return tableService.create(req);
    // }

    // @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // public TableResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTableRequest req) {
    //     return tableService.update(id, req);
    // }

    // @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // public ResponseEntity<Void> delete(@PathVariable Long id) {
    //     tableService.delete(id);
    //     return ResponseEntity.noContent().build();
    // }
    @GetMapping public ResponseEntity<List<TableResponse>> getAll(){ return ResponseEntity.ok(tableService.getAll()); }
    @PostMapping @PreAuthorize("hasAuthority('ROLE_ADMIN')")public ResponseEntity<TableResponse> create(@Valid @RequestBody CreateTableRequest req){ return ResponseEntity.ok(tableService.create(req)); }
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<TableResponse> update(@PathVariable Long id,@Valid @RequestBody UpdateTableRequest req){ return ResponseEntity.ok(tableService.update(id, req)); }
    @PatchMapping("/{id}/status")@PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')") public ResponseEntity<TableResponse> updateStatus(@PathVariable Long id,@Valid @RequestBody UpdateTableStatusRequest req){ return ResponseEntity.ok(tableService.updateStatus(id, req.getStatus())); }
    @DeleteMapping("/{id}")@PreAuthorize("hasAuthority('ROLE_ADMIN')") public ResponseEntity<Void> delete(@PathVariable Long id){ tableService.delete(id); return ResponseEntity.noContent().build(); }
}