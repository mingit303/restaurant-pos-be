package com.example.restaurant.dto.profile;

import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String email;
    private String phone;
}
