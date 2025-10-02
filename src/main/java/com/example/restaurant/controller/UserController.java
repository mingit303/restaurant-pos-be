package com.example.restaurant.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant.dto.user.UpdateUserStatusRequest;
import com.example.restaurant.dto.user.UserRequest;
import com.example.restaurant.dto.user.UserResponse;
import com.example.restaurant.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService us) { this.userService = us; }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponse create(@RequestBody UserRequest req) {
        return userService.create(req);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponse updateStatus(@PathVariable Long id, @RequestBody UpdateUserStatusRequest body) {
        return userService.updateStatus(id, body.status());
    }
}
