package com.example.restaurant.config;

import com.example.restaurant.dto.ErrorResponse;
import com.example.restaurant.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, ObjectMapper objectMapper) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfig()))
            //Tắt cơ chế login mặc định
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Cho phép public các tài nguyên tĩnh
                .requestMatchers("/images/**", "/uploads/**", "/favicon.ico").permitAll()

                //  Các API public khác
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(
                "/vnpay/**", 
                "/invoices/vnpay-return",
                "/invoices/vnpay-ipn",
                "/customers/**"
                ).permitAll()

                // Role-based routes
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/cashier/**").hasAnyAuthority("ROLE_CASHIER", "ROLE_ADMIN")
                .requestMatchers("/waiter/**").hasAnyAuthority("ROLE_WAITER", "ROLE_ADMIN")
                .requestMatchers("/kitchen/**").hasAnyAuthority("ROLE_KITCHEN", "ROLE_ADMIN")
                .requestMatchers("/invoices/**").hasAnyAuthority("ROLE_CASHIER", "ROLE_WAITER", "ROLE_ADMIN")
                .requestMatchers("/customers/**").hasAnyAuthority("ROLE_WAITER", "ROLE_ADMIN")
                // Mọi request còn lại yêu cầu đăng nhập
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfig() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.asList(allowedOrigins));
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(Arrays.asList("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            ErrorResponse error = new ErrorResponse(
                    401,
                    "Unauthorized - Please login",
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            response.setStatus(401);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), error);
        };
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            ErrorResponse error = new ErrorResponse(
                    403,
                    "Forbidden - You don't have permission",
                    request.getRequestURI(),
                    LocalDateTime.now()
            );
            response.setStatus(403);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), error);
        };
    }
}