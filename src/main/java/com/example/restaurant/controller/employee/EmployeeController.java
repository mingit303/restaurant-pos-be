package com.example.restaurant.controller.employee;

import com.example.restaurant.dto.employee.*;
import com.example.restaurant.service.employee.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // ADMIN: xem tất cả nhân viên
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ADMIN: xem chi tiết nhân viên
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.searchEmployees(keyword, gender, role, page, size));
    }



    // ADMIN: thêm nhân viên (kèm tài khoản)
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // NHÂN VIÊN: xem thông tin cá nhân
    @GetMapping("/me")
    public ResponseEntity<EmployeeResponse> getSelf(Authentication auth) {
        return ResponseEntity.ok(service.getSelf(auth.getName()));
    }

    // NHÂN VIÊN: cập nhật thông tin cá nhân
    @PatchMapping("/me")
    public ResponseEntity<EmployeeResponse> updateSelf(@Valid @RequestBody EmployeeRequest req, Authentication auth) {
        return ResponseEntity.ok(service.updateSelf(auth.getName(), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa nhân viên và tài khoản tương ứng!");
    }
}
