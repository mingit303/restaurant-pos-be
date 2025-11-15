package com.example.restaurant.dto.profile.Response;

import lombok.*;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ProfileResponse {
    private Long id;   
    private String username;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String citizenId;
    private String role;       // employeeId
    private String avatarUrl; // ảnh đại diện

}
