package com.example.restaurant.service;                           // 1

import com.example.restaurant.domain.table.*;                     // 2
import com.example.restaurant.dto.table.*;                        // 3
import com.example.restaurant.exception.*;                        // 4
import com.example.restaurant.repository.table.*;                 // 5
import org.springframework.stereotype.Service;                    // 6
import org.springframework.transaction.annotation.Transactional;  // 7
import java.util.*;                                               // 8

@Service                                                           // 9: Bean service Spring
public class TableService {

    private final RestaurantTableRepository tableRepo;             // 10

    public TableService(RestaurantTableRepository tableRepo) {     // 11
        this.tableRepo = tableRepo;
    }

    @Transactional(readOnly = true)                                // 12: Chỉ đọc
    public List<TableResponse> getAll() {                          // 13
        return tableRepo.findAll().stream()                        // 14
                .map(t -> new TableResponse(                       // 15
                        t.getId(), t.getCode(), t.getCapacity(), t.getStatus()
                ))
                .toList();                                         // 16
    }

    @Transactional                                                 // 17: Ghi DB
    public TableResponse updateStatus(Long id, String status) {    // 18
        RestaurantTable t = tableRepo.findById(id)                 // 19
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        TableStatus newStatus = TableStatus.valueOf(status);       // 20: parse enum (yêu cầu UPPERCASE)
        t.setStatus(newStatus);                                    // 21
        return new TableResponse(t.getId(), t.getCode(),           // 22
                t.getCapacity(), t.getStatus());
    }

    @Transactional
    public TableResponse create(CreateTableRequest req) {
        if (tableRepo.existsByCode(req.code())) {
            throw new ConflictException("Mã bàn đã tồn tại.");
        }
        RestaurantTable t = RestaurantTable.builder()
                .code(req.code())
                .capacity(req.capacity())
                .status(TableStatus.FREE)
                .build();
        tableRepo.save(t);
        return new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
    }

    @Transactional
    public TableResponse update(Long id, UpdateTableRequest req) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        // Nếu đổi sang code mới mà đã tồn tại thì báo lỗi
        if (!t.getCode().equals(req.code()) && tableRepo.existsByCode(req.code())) {
            throw new ConflictException("Mã bàn đã tồn tại.");
        }
        t.setCode(req.code());
        t.setCapacity(req.capacity());
        return new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        tableRepo.delete(t);
    }

}
