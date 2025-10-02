package com.example.restaurant.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/whoami")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> whoami(Authentication auth) {
        return Map.of(
            "user", auth.getName(),
            "roles", auth.getAuthorities().toString(),
            "message", "Only ADMIN can see this."
        );
    }

}
