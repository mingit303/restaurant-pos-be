package com.example.restaurant.ws;

import com.example.restaurant.domain.menu.MenuItem;
import com.example.restaurant.dto.menu.response.MenuItemResponse;
import com.example.restaurant.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuEventPublisher {

    private final SimpMessagingTemplate ws;

    private void send(String topic, Object payload) {
        ws.convertAndSend(topic, payload);
    }

    // Khi món được thêm/sửa/xóa 
    public void menuChanged(MenuItem item, String action) {
        try {
            MenuItemResponse dto =
            ("CREATED".equals(action) || "UPDATED".equals(action))
                ? MenuMapper.toResponseWithRecipe(item)
                : MenuMapper.toResponse(item); 
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("data", dto);
            payload.put("timestamp", LocalDateTime.now().toString());
            send("/topic/menu", payload);
        } catch (Exception e) {
            log.error("Error sending menuChanged WS: {}", e.getMessage(), e);
            send("/topic/menu", Map.of("action", "ERROR", "data", "[FAILED to convert MenuItem to DTO]"));
        }
    }
}
