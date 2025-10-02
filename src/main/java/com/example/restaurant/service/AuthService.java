package com.example.restaurant.service;

import com.example.restaurant.domain.user.User;
import com.example.restaurant.dto.auth.*;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.repository.user.UserRepository;
import com.example.restaurant.security.CustomUserDetails;
import com.example.restaurant.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authManager, UserRepository userRepo, JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse login(LoginRequest req) {
        // 1. Xác thực username + password
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 2. Lấy user từ DB để check status
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new BadRequestException("Sai tài khoản hoặc mật khẩu"));

        if (user.getStatus().getMessage() != null) {
            throw new BadRequestException(user.getStatus().getMessage());
        }

        // 3. Lấy roles từ Authentication
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 4. Sinh token
        String access = jwtUtils.generateAccessToken(user.getUsername(), roles);
        String refresh = jwtUtils.generateRefreshToken(user.getUsername());

        return new AuthResponse(access, refresh, "Bearer");
    }

    public AuthResponse refresh(RefreshRequest req) {
        // 1. Verify refresh token
        jwtUtils.verify(req.getRefreshToken());

        // 2. Extract username
        String username = jwtUtils.extractUsername(req.getRefreshToken());

        // 3. Check user còn tồn tại và ACTIVE
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User không tồn tại"));

        if (user.getStatus().getMessage() != null) {
            throw new BadRequestException(user.getStatus().getMessage());
        }

        // 4. Lấy roles từ DB
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());

        // 5. Sinh access mới, reuse refresh
        String access = jwtUtils.generateAccessToken(username, roles);

        return new AuthResponse(access, req.getRefreshToken(), "Bearer");
    }
}
