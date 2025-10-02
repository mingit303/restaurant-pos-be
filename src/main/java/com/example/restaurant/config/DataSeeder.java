package com.example.restaurant.config;

import com.example.restaurant.domain.user.Role;
import com.example.restaurant.domain.user.User;
import com.example.restaurant.domain.user.UserStatus;
import com.example.restaurant.repository.user.RoleRepository;
import com.example.restaurant.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.cashier.username}")
    private String cashierUsername;

    @Value("${app.cashier.password}")
    private String cashierPassword;

    @Value("${app.waiter.username}")
    private String waiterUsername;

    @Value("${app.waiter.password}")
    private String waiterPassword;

    @Value("${app.kitchen.username}")
    private String kitchenUsername;

    @Value("${app.kitchen.password}")
    private String kitchenPassword;

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepo,
                                   UserRepository userRepo,
                                   PasswordEncoder encoder) {
        return args -> {
            // Seed roles
            createRoleIfNotFound("ROLE_ADMIN", roleRepo);
            createRoleIfNotFound("ROLE_CASHIER", roleRepo);
            createRoleIfNotFound("ROLE_WAITER", roleRepo);
            createRoleIfNotFound("ROLE_KITCHEN", roleRepo);

            // Seed users
            createUserIfNotFound(adminUsername, adminPassword, "ROLE_ADMIN", userRepo, roleRepo, encoder, "Admin User");
            createUserIfNotFound(cashierUsername, cashierPassword, "ROLE_CASHIER", userRepo, roleRepo, encoder, "Cashier User");
            createUserIfNotFound(waiterUsername, waiterPassword, "ROLE_WAITER", userRepo, roleRepo, encoder, "Waiter User");
            createUserIfNotFound(kitchenUsername, kitchenPassword, "ROLE_KITCHEN", userRepo, roleRepo, encoder, "Kitchen User");
        };
    }

    private void createRoleIfNotFound(String roleName, RoleRepository roleRepo) {
        roleRepo.findByName(roleName).orElseGet(() ->
                roleRepo.save(new Role(null, roleName))
        );
    }

    private void createUserIfNotFound(String username,
                                      String password,
                                      String roleName,
                                      UserRepository userRepo,
                                      RoleRepository roleRepo,
                                      PasswordEncoder encoder,
                                      String fullName) {
        userRepo.findByUsername(username).orElseGet(() -> {
            Role role = roleRepo.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException(roleName + " not found"));
            User user = User.builder()
                    .username(username)
                    .password(encoder.encode(password))
                    .fullName(fullName)
                    .status(UserStatus.ACTIVE) // Kích hoạt ngay
                    .build();
            user.getRoles().add(role);
            return userRepo.save(user);
        });
    }
}
