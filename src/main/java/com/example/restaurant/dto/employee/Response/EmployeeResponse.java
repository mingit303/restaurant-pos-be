package com.example.restaurant.dto.employee.Response;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.User;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String citizenId;
    private String email;
    private String phone;
    private String position;
    private String avatarUrl;

    private Long userId;
    private String username;
    private String roleName;
    private String status;

    public static EmployeeResponse fromEntity(Employee e) {
        User u = e.getUser();
        return EmployeeResponse.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .gender(e.getGender())
                .birthDate(e.getBirthDate())
                .citizenId(e.getCitizenId())
                .email(e.getEmail())
                .phone(e.getPhone())
                .position(e.getPosition())
                .avatarUrl(e.getAvatarUrl() != null ? e.getAvatarUrl() : "/images/avatars/default-avatar.png")
                .userId(u != null ? u.getId() : null)
                .username(u != null ? u.getUsername() : null)
                .roleName(u != null && u.getRole() != null ? u.getRole().getName() : null)
                .status(u != null ? u.getStatus().name() : null)
                .build();
    }
}
