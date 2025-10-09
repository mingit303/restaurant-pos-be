package com.example.restaurant.service;

import com.example.restaurant.domain.user.*;
import com.example.restaurant.dto.user.*;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.user.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder, EmployeeRepository employeeRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.employeeRepo = employeeRepo;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepo.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        if (req.getRole() != null) {
            Role role = roleRepo.findByName(req.getRole())
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
            user.setRole(role);
        }

        if (req.getStatus() != null) {
            user.setStatus(UserStatus.valueOf(req.getStatus()));
        }

        userRepo.save(user);
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Set null user_id bên employee (nếu có)
        employeeRepo.findByUser(user).ifPresent(emp -> {
            emp.setUser(null);
            employeeRepo.save(emp);
        });

        userRepo.delete(user);
    }
    
    // ✅ TÌM KIẾM VÀ LỌC AN TOÀN
    // @Transactional(readOnly = true)
    // public List<UserResponse> searchUsers(String keyword, String role, String status) {
    //     // Xử lý chuỗi null, rỗng hoặc "null"
    //     keyword = (keyword == null || keyword.isBlank() || keyword.equalsIgnoreCase("null")) ? null : keyword.trim();
    //     role = (role == null || role.isBlank() || role.equalsIgnoreCase("null")) ? null : role.trim();
    //     status = (status == null || status.isBlank() || status.equalsIgnoreCase("null")) ? null : status.trim();

    //     UserStatus statusEnum = null;
    //     if (status != null) {
    //         try {
    //             statusEnum = UserStatus.valueOf(status);
    //         } catch (IllegalArgumentException e) {
    //             statusEnum = null; // bỏ qua nếu sai
    //         }
    //     }

    //     return userRepo.searchUsers(keyword, role, statusEnum)
    //             .stream()
    //             .map(UserResponse::fromEntity)
    //             .collect(Collectors.toList());
    // }
    @Transactional(readOnly = true)
public Map<String, Object> searchUsers(String keyword, String role, String status, int page, int size) {
    keyword = (keyword == null || keyword.isBlank() || keyword.equalsIgnoreCase("null")) ? null : keyword.trim();
    role = (role == null || role.isBlank() || role.equalsIgnoreCase("null")) ? null : role.trim();
    status = (status == null || status.isBlank() || status.equalsIgnoreCase("null")) ? null : status.trim();

    UserStatus statusEnum = null;
    if (status != null) {
        try {
            statusEnum = UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            statusEnum = null;
        }
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<User> userPage = userRepo.searchUsers(keyword, role, statusEnum, pageable);

    Map<String, Object> result = new HashMap<>();
    result.put("content", userPage.getContent().stream().map(UserResponse::fromEntity).toList());
    result.put("currentPage", userPage.getNumber());
    result.put("totalPages", userPage.getTotalPages());
    result.put("totalElements", userPage.getTotalElements());
    return result;
    }

}
