package com.example.restaurant.service.auth;

import com.example.restaurant.domain.user.*;
import com.example.restaurant.dto.auth.*;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.repository.user.UserRepository;
import com.example.restaurant.security.JwtUtils;

import org.springframework.security.authentication.*;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepo;

    public AuthService(AuthenticationManager authManager,
                       JwtUtils jwtUtils,
                       UserRepository userRepo) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
  
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            // 🔥 Trả lỗi 401 (sai mật khẩu)
            throw new BadRequestException("Sai tên đăng nhập hoặc mật khẩu!");
        }

        // UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng"));

        // 🟡 Nếu là lần đầu (PENDING) → tự kích hoạt
        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            userRepo.save(user);
        } 
        // 🚫 Nếu tạm khóa / vô hiệu hóa → chặn login
        else if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new RuntimeException("Tài khoản đang bị tạm khóa.");
        } else if (user.getStatus() == UserStatus.DISABLED) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa.");
        }

        // ✅ Sinh token
        String roleName = user.getRole().getName();
        String accessToken = jwtUtils.generateAccessToken(user.getUsername(), roleName);
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .username(user.getUsername())
                .roleName(roleName)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenRefreshResponse refresh(String refreshToken) {
        String username = jwtUtils.extractUsername(refreshToken);
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String roleName = user.getRole().getName();

        String newAccess = jwtUtils.generateAccessToken(username, roleName);
        return new TokenRefreshResponse(newAccess);
    }
}
