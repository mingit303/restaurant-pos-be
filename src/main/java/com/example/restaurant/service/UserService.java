package com.example.restaurant.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restaurant.domain.user.Role;
import com.example.restaurant.domain.user.User;
import com.example.restaurant.domain.user.UserStatus;
import com.example.restaurant.dto.user.UserRequest;
import com.example.restaurant.dto.user.UserResponse;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.user.RoleRepository;
import com.example.restaurant.repository.user.UserRepository;

@Service 
@Transactional
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo; this.roleRepo = roleRepo; this.encoder = encoder;
    }

    public UserResponse create(UserRequest req) {
        Set<Role> roles = req.roles().stream()
            .map(name -> roleRepo.findByName(name)
                .orElseThrow(() -> new NotFoundException("Role không tồn tại: " + name)))
            .collect(Collectors.toSet());

        User u = User.builder()
            .username(req.username())
            .password(encoder.encode(req.password()))
            .roles(roles)
            .status(UserStatus.PENDING) // mặc định
            .build();
        userRepo.save(u);
        return toDto(u);
    }

    public UserResponse updateStatus(Long id, String status) {
        User u = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User không tồn tại"));
        UserStatus newStatus = UserStatus.valueOf(status);
        u.setStatus(newStatus);
        return toDto(u);
    }

    private UserResponse toDto(User u) {
        return new UserResponse(
            u.getId(),
            u.getUsername(),
            u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
            u.getStatus().name()
        );
    }
}
