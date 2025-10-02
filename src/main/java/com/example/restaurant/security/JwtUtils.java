package com.example.restaurant.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtils {
    @Value("${app.jwt.secret}") 
    private String secret;

    @Value("${app.jwt.issuer}") 
    private String issuer;

    @Value("${app.jwt.access-expiration-ms}") 
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}") 
    private long refreshExpirationMs;

    private Algorithm algorithm() { return Algorithm.HMAC256(secret); }

    public String generateAccessToken(String username, Collection<String> roles) {
        Date now = new Date(); 
        Date exp = new Date(now.getTime()+accessExpirationMs);
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("roles", new ArrayList<>(roles))
                .sign(algorithm());
    }

    public String generateRefreshToken(String username) {
        Date now = new Date(); Date exp = new Date(now.getTime()+refreshExpirationMs);
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm());
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm()).withIssuer(issuer).build().verify(token);
    }

    public String extractUsername(String token) { return verify(token).getSubject(); }
}
