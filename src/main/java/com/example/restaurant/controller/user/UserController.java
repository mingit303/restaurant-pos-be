package com.example.restaurant.controller.user;

import com.example.restaurant.dto.user.*;
import com.example.restaurant.service.user.UserService;

import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 🔹 Lấy danh sách user (ADMIN)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 Cập nhật role hoặc status (ADMIN)
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(service.updateUser(id, req));
    }

    // 🔹 ADMIN đặt lại mật khẩu cho nhân viên
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest req) {
        service.resetPassword(id, req.getNewPassword());
        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Đã xóa tài khoản, nhân viên giữ nguyên!");
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.searchUsers(keyword, role, status, page, size));
    }

}
