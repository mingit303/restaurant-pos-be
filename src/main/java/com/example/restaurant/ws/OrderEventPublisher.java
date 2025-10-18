package com.example.restaurant.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.restaurant.domain.order.Order;
import com.example.restaurant.domain.order.OrderItem;
import com.example.restaurant.dto.order.response.OrderResponse;
import com.example.restaurant.mapper.OrderMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gửi sự kiện WebSocket cho FE khi Order có thay đổi (order mới, thêm món, bếp cập nhật...)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final SimpMessagingTemplate ws;

    /** 📡 Hàm gửi dữ liệu WS ra FE */
    private void send(String topic, Object payload) {
        log.info("📡 WS -> {} : {}", topic, payload);
        ws.convertAndSend(topic, payload);
    }

    /** 🔔 Khi Order thay đổi: CREATED / ITEM_ADDED / ITEM_STATE_UPDATED / ORDER_SERVED ... */
    // public void orderChanged(Long orderId, String action, Object data) {
    //     System.out.println("📡 orderChanged() called → orderId=" + orderId + ", action=" + action + ", data=" + data);
    //     send("/topic/orders", Map.of(
    //             "action", action,
    //             "orderId", orderId,
    //             "data", data,
    //             "timestamp", LocalDateTime.now().toString()
    //     ));
    // }    
    // public void orderChanged(Long orderId, String action, Object data) {
    //     log.info("📡 orderChanged() called → orderId={}, action={}, data={}", orderId, action, data);
    //     Map<String, Object> payload = new HashMap<>();
    //     payload.put("action", action);
    //     payload.put("orderId", orderId);
    //     payload.put("data", data); // có thể null mà không crash
    //     payload.put("timestamp", LocalDateTime.now().toString());

    //     send("/topic/orders", payload);
    // }
     public void orderChanged(Order order, String action) {
        try {
            OrderResponse dto = OrderMapper.toResponse(order);
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("orderId", order.getId());
            payload.put("data", dto);
            payload.put("timestamp", LocalDateTime.now().toString());
            send("/topic/orders", payload);
        } catch (Exception e) {
            log.error("❌ Error sending orderChanged WS: {}", e.getMessage(), e);
            send("/topic/orders", Map.of("action", "ERROR", "data", "[FAILED to convert Order to DTO]"));
        }
    }

    /** 🔔 Khi một món trong Order thay đổi (ví dụ bếp cập nhật trạng thái món) */
    public void orderItemChanged(OrderItem item, String action) {
        try {
            OrderResponse dto = OrderMapper.toResponse(item.getOrder());
            Map<String, Object> payload = new HashMap<>();
            payload.put("action", action);
            payload.put("orderId", item.getOrder().getId());
            payload.put("data", dto);
            payload.put("timestamp", LocalDateTime.now().toString());
            send("/topic/orders", payload);
        } catch (Exception e) {
            log.error("❌ Error sending orderItemChanged WS: {}", e.getMessage(), e);
            send("/topic/orders", Map.of("action", "ERROR", "data", "[FAILED to convert OrderItem to DTO]"));
        }
    }
}
