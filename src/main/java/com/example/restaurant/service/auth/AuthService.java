package com.example.restaurant.service.auth;

import com.example.restaurant.domain.user.*;
import com.example.restaurant.dto.auth.*;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.repository.user.*;
import com.example.restaurant.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;

    public AuthService(AuthenticationManager authManager,
                       JwtUtils jwtUtils,
                       UserRepository userRepo,
                       RefreshTokenRepository refreshTokenRepo) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    /** 🔑 Đăng nhập */
    @Transactional
    public AuthResponse login(LoginRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy tài khoản người dùng"));

        // 🚫 Kiểm tra trạng thái tài khoản
        if (user.getStatus() == UserStatus.SUSPENDED)
            throw new BadRequestException("Tài khoản đang bị tạm khóa.");
        if (user.getStatus() == UserStatus.DISABLED)
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa.");
        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            userRepo.save(user);
        }

        // 🧠 Thu hồi toàn bộ refresh token cũ (nếu có)
        refreshTokenRepo.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()) && !t.isRevoked())
                .forEach(t -> {
                    t.setRevoked(true);
                    refreshTokenRepo.save(t);
                });

        // ✅ Sinh token mới
        String roleName = user.getRole().getName();
        String accessToken = jwtUtils.generateAccessToken(user.getUsername(), roleName);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        // ✅ Lưu refresh token mới vào DB
        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtUtils.getRefreshExpirationMs()))
                .revoked(false)
                .build();
        refreshTokenRepo.save(tokenEntity);

        // ✅ Trả response
        return AuthResponse.builder()
                .username(user.getUsername())
                .roleName(roleName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /** ♻️ Làm mới Access Token */
    @Transactional(readOnly = true)
    public TokenRefreshResponse refresh(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepo.findByTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh token không hợp lệ hoặc đã bị thu hồi."));

        // Kiểm tra hạn dùng
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token đã hết hạn. Vui lòng đăng nhập lại.");
        }

        User user = tokenEntity.getUser();
        String roleName = user.getRole().getName();
        String newAccess = jwtUtils.generateAccessToken(user.getUsername(), roleName);

        return new TokenRefreshResponse(newAccess);
    }

    /** 🚪 Đăng xuất (thu hồi token trong DB) */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepo.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                });
    }
}
