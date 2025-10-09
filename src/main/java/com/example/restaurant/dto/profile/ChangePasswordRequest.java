package com.example.restaurant.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordRequest {
    
    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    private String oldPassword;
    
    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    private String newPassword;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
    private String confirmPassword;
}
