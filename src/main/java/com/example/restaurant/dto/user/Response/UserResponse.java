package com.example.restaurant.dto.user.Response;

import com.example.restaurant.domain.user.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private String status;

    public static UserResponse fromEntity(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .role(u.getRole()!=null ? u.getRole().getName() : null)
                .status(u.getStatus().name())
                .build();
    }
}
