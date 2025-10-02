package com.example.restaurant.config;                         // 1

import org.springframework.context.annotation.Configuration;   // 2
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // 3

@Configuration                                                 // 4
@EnableMethodSecurity(prePostEnabled = true)                   // 5: Bật @PreAuthorize
public class MethodSecurityConfig {                            // 6
}
