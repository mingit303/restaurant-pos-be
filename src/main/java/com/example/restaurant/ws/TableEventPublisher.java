package com.example.restaurant.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TableEventPublisher {

    private final SimpMessagingTemplate ws;

    // Gửi dữ liệu realtime tới FE 
    public void send(String topic, Object payload) {
        ws.convertAndSend(topic, payload);
    }

    // Khi bàn được tạo hoặc cập nhật
    public void tableChanged(Long id, String code, Integer capacity, String status, String type) {
        send("/topic/tables", Map.of(
                "type", type, // CREATED, UPDATED, STATUS_CHANGED
                "tableId", id,
                "code", code,
                "capacity", capacity,
                "status", status,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // Khi bàn bị xóa
    public void tableDeleted(Long id, String code) {
        send("/topic/tables/delete", Map.of(
            "action", "DELETED",
            "tableId", id,
            "code", code,
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
