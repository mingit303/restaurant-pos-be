package com.example.restaurant.dto.profile;

import lombok.*;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProfileResponse {
    private String username;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String role;
}
