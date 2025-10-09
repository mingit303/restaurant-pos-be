package com.example.restaurant.config;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.*;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepo,
                               UserRepository userRepo,
                               EmployeeRepository empRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // ✅ Tạo role nếu chưa có
            List<String> roles = List.of("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_WAITER", "ROLE_KITCHEN");
            for (String r : roles)
                roleRepo.findByName(r).orElseGet(() -> roleRepo.save(Role.builder().name(r).build()));

            // ✅ Tạo dữ liệu người dùng
            create("admin", "123456", "ROLE_ADMIN", "Nguyễn Văn Admin", "MALE",
                    "012345678901", "admin@sushi.vn", "0901111111", "Quản lý",
                    LocalDate.of(1990, 1, 1), roleRepo, userRepo, empRepo, encoder);

            create("cashier", "123456", "ROLE_CASHIER", "Trần Thị Thu Ngân", "FEMALE",
                    "012345678902", "cashier@sushi.vn", "0902222222", "Thu ngân",
                    LocalDate.of(1995, 5, 15), roleRepo, userRepo, empRepo, encoder);

            create("waiter", "123456", "ROLE_WAITER", "Phạm Minh Quân", "MALE",
                    "012345678903", "waiter@sushi.vn", "0903333333", "Phục vụ",
                    LocalDate.of(1998, 7, 20), roleRepo, userRepo, empRepo, encoder);

            create("kitchen", "123456", "ROLE_KITCHEN", "Lê Thị Bích Hằng", "FEMALE",
                    "012345678904", "kitchen@sushi.vn", "0904444444", "Bếp chính",
                    LocalDate.of(1992, 3, 10), roleRepo, userRepo, empRepo, encoder);
        };
    }

    private void create(String username, String pass, String roleName,
                        String fullName, String gender, String citizenId,
                        String email, String phone, String pos, LocalDate birth,
                        RoleRepository roleRepo, UserRepository userRepo,
                        EmployeeRepository empRepo, PasswordEncoder encoder) {

        if (userRepo.findByUsername(username).isPresent()) return;

        // ✅ Lấy role tương ứng
        Role role = roleRepo.findByName(roleName).orElseThrow();

        // ✅ Tạo và lưu user trước
        User user = User.builder()
                .username(username)
                .password(encoder.encode(pass))
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepo.save(user); // có ID, managed entity

        // ✅ Tạo employee trỏ tới user đó
        Employee emp = Employee.builder()
                .fullName(fullName)
                .gender(gender)
                .birthDate(birth)
                .citizenId(citizenId)
                .email(email)
                .phone(phone)
                .position(pos)
                .user(user)
                .build();

        empRepo.save(emp); // Hibernate sẽ link user_id = user.id
    }
}
