package com.example.restaurant.service.employee;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.*;
import com.example.restaurant.dto.employee.*;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.user.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public EmployeeService(EmployeeRepository e, UserRepository u, RoleRepository r, PasswordEncoder encoder) {
        this.employeeRepo = e; this.userRepo = u; this.roleRepo = r; this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepo.findAll().stream().map(EmployeeResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        return EmployeeResponse.fromEntity(e);
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        Role role = roleRepo.findByName(req.getRole())
                .orElseThrow(() -> new RuntimeException("Role không hợp lệ"));

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .status(UserStatus.PENDING)
                .role(role)
                .build();
        userRepo.save(user);

        Employee e = Employee.builder()
                .fullName(req.getFullName())
                .gender(req.getGender())
                .birthDate(req.getBirthDate())
                .citizenId(req.getCitizenId())
                .email(req.getEmail())
                .phone(req.getPhone())
                .position(req.getPosition())
                .user(user)
                .build();
        employeeRepo.save(e);

        return EmployeeResponse.fromEntity(e);
    }

    @Transactional
    public EmployeeResponse updateSelf(String username, EmployeeRequest req) {
        Employee e = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        e.setFullName(req.getFullName());
        e.setGender(req.getGender());
        e.setBirthDate(req.getBirthDate());
        e.setCitizenId(req.getCitizenId());
        e.setEmail(req.getEmail());
        e.setPhone(req.getPhone());
        e.setPosition(req.getPosition());
        employeeRepo.save(e);
        return EmployeeResponse.fromEntity(e);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getSelf(String username) {
        Employee e = employeeRepo.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        return EmployeeResponse.fromEntity(e);
    }

    @Transactional
    public void delete(Long id) {
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        User user = e.getUser();
        if (user != null) {
            userRepo.delete(user);
        }
        employeeRepo.delete(e);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> searchEmployees(String keyword, String gender, String role, int page, int size) {
        keyword = (keyword == null || keyword.isBlank() || keyword.equalsIgnoreCase("null")) ? null : keyword.trim();
        gender = (gender == null || gender.isBlank() || gender.equalsIgnoreCase("null")) ? null : gender.trim();
        role = (role == null || role.isBlank() || role.equalsIgnoreCase("null")) ? null : role.trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Employee> empPage = employeeRepo.searchEmployees(keyword, gender, role, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", empPage.getContent().stream().map(EmployeeResponse::fromEntity).toList());
        result.put("currentPage", empPage.getNumber());
        result.put("totalPages", empPage.getTotalPages());
        result.put("totalElements", empPage.getTotalElements());
        return result;
    }
}
