package com.example.restaurant.controller.menu;

import com.example.restaurant.dto.menu.request.MenuItemRequest;
import com.example.restaurant.dto.menu.response.MenuItemResponse;
import com.example.restaurant.service.menu.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/menu") @RequiredArgsConstructor
public class MenuController {
    private final MenuService menu;

    @GetMapping("/items")
    public ResponseEntity<Page<MenuItemResponse>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Long categoryId
    ){ return ResponseEntity.ok(menu.list(page,size,keyword,categoryId)); }

    @PostMapping("/items")
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest req){
        return ResponseEntity.ok(menu.create(req));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItemResponse> update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest req){
        return ResponseEntity.ok(menu.update(id,req));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        menu.delete(id); return ResponseEntity.noContent().build();
    }

    @PostMapping("/items/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File trống!");
        }
            System.out.println("Working dir: " + System.getProperty("user.dir"));
        // ✅ Thư mục lưu ảnh
        Path uploadDir = Paths.get("uploads/images/menu");
        Files.createDirectories(uploadDir);

        // ✅ Giữ lại phần mở rộng file (ví dụ: .jpg, .png, .jpeg)
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        } else {
            // Nếu không có extension, mặc định là .jpg
            extension = ".jpg";
        }

        // ✅ Tạo tên file duy nhất
        String filename = UUID.randomUUID() + extension;

        // ✅ Lưu file vào thư mục static
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // ✅ Trả về URL để FE hiển thị
        String imageUrl = "/images/menu/" + filename;
        
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}
