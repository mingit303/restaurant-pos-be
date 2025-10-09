package com.example.restaurant.service;

import com.example.restaurant.domain.table.*;
import com.example.restaurant.dto.table.*;
import com.example.restaurant.exception.*;
import com.example.restaurant.repository.table.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class TableService {

    private final RestaurantTableRepository tableRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public TableService(RestaurantTableRepository tableRepo, SimpMessagingTemplate messagingTemplate) {
        this.tableRepo = tableRepo;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public List<TableResponse> getAll() {
        return tableRepo.findAll().stream()
                .map(t -> new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus()))
                .toList();
    }

    @Transactional
    public TableResponse updateStatus(Long id, String status) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        TableStatus newStatus = TableStatus.valueOf(status);
        t.setStatus(newStatus);

        TableResponse res = new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());

        // publish realtime
        messagingTemplate.convertAndSend("/topic/tables", res);

        return res;
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

        TableResponse res = new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
        messagingTemplate.convertAndSend("/topic/tables", res);

        return res;
    }

    @Transactional
    public TableResponse update(Long id, UpdateTableRequest req) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        if (!t.getCode().equals(req.code()) && tableRepo.existsByCode(req.code())) {
            throw new ConflictException("Mã bàn đã tồn tại.");
        }
        t.setCode(req.code());
        t.setCapacity(req.capacity());

        TableResponse res = new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
        messagingTemplate.convertAndSend("/topic/tables", res);

        return res;
    }

    @Transactional
    public void delete(Long id) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));
        tableRepo.delete(t);

        // Thông báo xóa
        messagingTemplate.convertAndSend("/topic/tables/delete", id);
    }
}
