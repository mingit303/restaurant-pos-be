package com.example.restaurant.service.employee;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.*;
import com.example.restaurant.dto.employee.Request.EmployeeRequest;
import com.example.restaurant.dto.employee.Response.EmployeeResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.invoice.InvoiceRepository;
import com.example.restaurant.repository.order.OrderItemRepository;
import com.example.restaurant.repository.order.OrderRepository;
import com.example.restaurant.repository.user.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service @RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final InvoiceRepository invoiceRepo;

    // Thư mục lưu file thực tế
    private static final String UPLOAD_PATH = "uploads/images/avatars/";


    private boolean isDefaultAvatar(String url) {
        return url == null || url.endsWith("default-avatar.png");
    }

    private boolean isStoredAvatar(String url) {
        return url != null && url.startsWith("/images/avatars/");
    }

    private boolean isValidImage(MultipartFile file) {
        if (file == null || file.getContentType() == null) return false;
        return switch (file.getContentType()) {
            case "image/png", "image/jpeg", "image/jpg", "image/webp" -> true;
            default -> false;
        };
    }

    private String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepo.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .toList();
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
                .avatarUrl("/images/avatars/default-avatar.png")   // URL đúng để FE load
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
            .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên."));

        // Waiter → Order
        if (orderRepo.existsByWaiter_Id(id)) {
            throw new BadRequestException("Không thể xóa. Nhân viên đã phục vụ các order.");
        }

        // Chef → OrderItem
        if (orderItemRepo.existsByChef_Id(id)) {
            throw new BadRequestException("Không thể xóa. Nhân viên đã nấu món trong đơn món.");
        }

        // Cashier → Invoice
        if (invoiceRepo.existsByCashier_Id(id)) {
            throw new BadRequestException("Không thể xóa. Nhân viên đã thu ngân hóa đơn.");
        }

        // Xóa avatar file
        String avatar = e.getAvatarUrl();

        if (isStoredAvatar(avatar) && !isDefaultAvatar(avatar)) {
            try {
                Path filePath = Paths.get(UPLOAD_PATH + fileNameFromUrl(avatar));
                Files.deleteIfExists(filePath);
            } catch (Exception ex) {
                System.out.println("Không thể xóa avatar cũ: " + ex.getMessage());
            }
        }

        // Xóa User nếu có
        if (e.getUser() != null) {
            userRepo.delete(e.getUser());
        }

        employeeRepo.delete(e);
    }



    @Transactional(readOnly = true)
    public Map<String, Object> searchEmployees(String keyword, String gender, String role, int page, int size) {

        keyword = (keyword == null || keyword.isBlank() ? null : keyword.trim());
        gender  = (gender == null || gender.isBlank() ? null : gender.trim());
        role    = (role == null || role.isBlank() ? null : role.trim());

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Employee> empPage = employeeRepo.searchEmployees(keyword, gender, role, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", empPage.getContent().stream().map(EmployeeResponse::fromEntity).toList());
        result.put("currentPage", empPage.getNumber());
        result.put("totalPages", empPage.getTotalPages());
        result.put("totalElements", empPage.getTotalElements());

        return result;
    }


    @Transactional
    public EmployeeResponse uploadAvatar(Long id, MultipartFile file) {

        if (!isValidImage(file)) {
            throw new RuntimeException("Chỉ hỗ trợ PNG, JPG, JPEG, WEBP");
        }

        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        try {
            Path uploadDir = Paths.get(UPLOAD_PATH);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Xóa avatar cũ
            String oldAvatar = e.getAvatarUrl();
            if (isStoredAvatar(oldAvatar) && !isDefaultAvatar(oldAvatar)) {
                Files.deleteIfExists(uploadDir.resolve(fileNameFromUrl(oldAvatar)));
            }

            // Tạo tên file mới
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains(".")) 
                    ? original.substring(original.lastIndexOf(".")) 
                    : ".png";

            String newName = id + "-" + System.currentTimeMillis() + ext;
            Path newPath = uploadDir.resolve(newName);

            Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);

            // Cập nhật URL đúng chuẩn WebConfig
            e.setAvatarUrl("/images/avatars/" + newName);
            employeeRepo.save(e);

            return EmployeeResponse.fromEntity(e);

        } catch (Exception ex) {
            throw new RuntimeException("Không thể upload avatar", ex);
        }
    }
}