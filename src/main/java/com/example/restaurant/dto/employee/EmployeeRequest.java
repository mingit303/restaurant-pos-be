package com.example.restaurant.dto.employee;

import lombok.Data;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class EmployeeRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate birthDate;
    
    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "\\d{9,12}", message = "CCCD phải gồm 9-12 chữ số")
    private String citizenId;

    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Pattern(regexp = "\\d{10,11}", message = "Số điện thoại phải có 10–11 chữ số")
    private String phone;
    private String position;

    // Dành cho user

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Role không được để trống")
    private String role;
}
