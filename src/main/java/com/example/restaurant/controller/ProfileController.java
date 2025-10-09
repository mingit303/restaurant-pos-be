package com.example.restaurant.controller;

import com.example.restaurant.dto.profile.*;
import com.example.restaurant.security.CustomUserDetails;
import com.example.restaurant.service.ProfileService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileResponse getProfile(@AuthenticationPrincipal CustomUserDetails details) {
        return service.getProfile(details.getUser());
    }

    @PatchMapping
    public ProfileResponse updateProfile(@AuthenticationPrincipal CustomUserDetails details,
                                        @RequestBody UpdateProfileRequest req) {
        return service.updateProfile(details.getUser(), req);
    }

    @PatchMapping("/change-password")
    public void changePassword(@AuthenticationPrincipal CustomUserDetails details,
                                @Valid @RequestBody ChangePasswordRequest req) {
        service.changePassword(details.getUser(), req);
    }

}
