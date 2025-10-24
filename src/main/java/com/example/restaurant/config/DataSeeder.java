package com.example.restaurant.config;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.menu.MenuCategory;
import com.example.restaurant.domain.table.RestaurantTable;
import com.example.restaurant.domain.table.TableStatus;
import com.example.restaurant.domain.user.*;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.menu.MenuCategoryRepository;
import com.example.restaurant.repository.table.RestaurantTableRepository;
import com.example.restaurant.repository.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class DataSeeder {

    @Bean
        CommandLineRunner initData(RoleRepository roleRepo,
                                UserRepository userRepo,
                                EmployeeRepository empRepo,
                                RestaurantTableRepository restaurantTableRepository,
                                MenuCategoryRepository menuCategoryRepository,
                                PasswordEncoder encoder) {
        return args -> {
                log.info("🚀 Starting data seeding...");

                // --- 🍽️ Bảng RestaurantTable ---
                if (restaurantTableRepository.count() == 0) {
                log.info("🧩 Seeding restaurant tables...");
                restaurantTableRepository.save(RestaurantTable.builder()
                        .code("T01")
                        .capacity(4)
                        .status(TableStatus.FREE)
                        .build());
                restaurantTableRepository.save(RestaurantTable.builder()
                        .code("T02")
                        .capacity(2)
                        .status(TableStatus.FREE)
                        .build());
                } else {
                log.info("✅ Restaurant tables đã tồn tại, bỏ qua seeding.");
                }

                // --- 🍣 Bảng MenuCategory ---
                if (menuCategoryRepository.count() == 0) {
                log.info("🧩 Seeding menu categories...");
                menuCategoryRepository.saveAll(List.of(
                        MenuCategory.builder().name("Nigiri").build(),
                        MenuCategory.builder().name("Maki").build(),
                        MenuCategory.builder().name("Sashimi").build(),
                        MenuCategory.builder().name("Drinks").build()
                ));
                } else {
                log.info("✅ Menu categories đã tồn tại, bỏ qua seeding.");
                }

                // --- 🧑‍💻 Roles ---
                List<String> roles = List.of("ROLE_ADMIN", "ROLE_CASHIER", "ROLE_WAITER", "ROLE_KITCHEN");
                roles.forEach(r ->
                roleRepo.findByName(r).orElseGet(() -> {
                        log.info("🆕 Creating role: {}", r);
                        return roleRepo.save(Role.builder().name(r).build());
                })
                );

                // --- 👥 Employees + Users ---
                List<Map<String, Object>> employees = List.of(
                Map.of("username", "admin", "password", "123456", "role", "ROLE_ADMIN",
                        "name", "Nguyễn Văn Admin", "gender", "MALE", "citizenId", "012345678901",
                        "email", "admin@sushi.vn", "phone", "0901111111", "position", "Quản lý",
                        "birth", LocalDate.of(1990, 1, 1)),
                Map.of("username", "cashier", "password", "123456", "role", "ROLE_CASHIER",
                        "name", "Trần Thị Thu Ngân", "gender", "FEMALE", "citizenId", "012345678902",
                        "email", "cashier@sushi.vn", "phone", "0902222222", "position", "Thu ngân",
                        "birth", LocalDate.of(1995, 5, 15)),
                Map.of("username", "waiter", "password", "123456", "role", "ROLE_WAITER",
                        "name", "Phạm Minh Quân", "gender", "MALE", "citizenId", "012345678903",
                        "email", "waiter@sushi.vn", "phone", "0903333333", "position", "Phục vụ",
                        "birth", LocalDate.of(1998, 7, 20)),
                Map.of("username", "kitchen", "password", "123456", "role", "ROLE_KITCHEN",
                        "name", "Lê Thị Bích Hằng", "gender", "FEMALE", "citizenId", "012345678904",
                        "email", "kitchen@sushi.vn", "phone", "0904444444", "position", "Bếp chính",
                        "birth", LocalDate.of(1992, 3, 10)));


                for (var e : employees) {
                String username = (String) e.get("username");
                if (userRepo.findByUsername(username).isPresent()) {
                        log.info("⚠️ User {} đã tồn tại, bỏ qua.", username);
                        continue;
                }

                Role role = roleRepo.findByName((String) e.get("role")).orElseThrow();

                User user = User.builder()
                        .username(username)
                        .password(encoder.encode((String) e.get("password")))
                        .role(role)
                        .status(UserStatus.ACTIVE)
                        .build();
                user = userRepo.save(user);

                Employee emp = Employee.builder()
                        .fullName((String) e.get("name"))
                        .gender((String) e.get("gender"))
                        .birthDate((LocalDate) e.get("birth"))
                        .citizenId((String) e.get("citizenId"))
                        .email((String) e.get("email"))
                        .phone((String) e.get("phone"))
                        .position((String) e.get("position"))
                        .user(user)
                        .build();
                empRepo.save(emp);

                log.info("✅ Đã tạo nhân viên: {} ({})", emp.getFullName(), role.getName());
                }

                log.info("🎉 Data seeding hoàn tất!");
        };
        }
}
