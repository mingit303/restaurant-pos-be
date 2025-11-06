package com.example.restaurant.service.profile;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.User;
import com.example.restaurant.dto.profile.Request.ChangePasswordRequest;
import com.example.restaurant.dto.profile.Request.UpdateProfileRequest;
import com.example.restaurant.dto.profile.Response.ProfileResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public ProfileResponse getProfile(User user) {
        Employee e = user.getEmployee();
        if (e == null) {
            throw new BadRequestException("Không tìm thấy thông tin nhân viên");
        }
        return new ProfileResponse(
                user.getUsername(),
                e.getFullName(),
                e.getGender(),
                e.getBirthDate(),
                e.getEmail(),
                e.getPhone(),
                user.getRole().getName()
        );
    }

    @Transactional
    public ProfileResponse updateProfile(User user, UpdateProfileRequest req) {
        Employee e = user.getEmployee();
        if (e == null) throw new BadRequestException("Không tìm thấy thông tin nhân viên");

        e.setFullName(req.getFullName());
        e.setGender(req.getGender());
        e.setBirthDate(req.getBirthDate());
        e.setEmail(req.getEmail());
        e.setPhone(req.getPhone());

        employeeRepo.save(e);

        return getProfile(user);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest req) {
        if (!encoder.matches(req.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Mật khẩu cũ không chính xác");

        if (!req.getNewPassword().equals(req.getConfirmPassword()))
            throw new BadRequestException("Mật khẩu mới không khớp");

        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }
}
