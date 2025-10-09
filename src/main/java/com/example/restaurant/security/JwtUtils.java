package com.example.restaurant.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-expiration-ms:3600000}") // 1 giờ
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // 7 ngày
    private long refreshExpirationMs;

    @Value("${app.jwt.issuer:SushiRestaurant}")
    private String issuer;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(jwtSecret.getBytes());
    }

    // ✅ Sinh Access Token (chỉ 1 role)
    public String generateAccessToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpirationMs);

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("role", role)
                .sign(algorithm());
    }

    // ✅ Sinh Refresh Token (chỉ cần username)
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpirationMs);

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm());
    }

    // ✅ Giải mã token
    private DecodedJWT decodeToken(String token) {
        return JWT.require(algorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    // ✅ Lấy username
    public String extractUsername(String token) {
        try {
            return decodeToken(token).getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    // ✅ Lấy role
    public String extractRole(String token) {
        try {
            return decodeToken(token).getClaim("role").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    // ✅ Kiểm tra token hợp lệ
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            return decodeToken(token).getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            return true;
        }
    }
}
