package com.example.restaurant.ws;

import com.example.restaurant.domain.invoice.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InvoiceEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void invoiceChanged(Invoice inv, String event) {
        Map<String, Object> payload = Map.of(
            "id", inv.getId(),
            "orderId", inv.getOrder().getId(),
            "status", inv.getStatus().name(),
            "event", event
        );

        // Gửi đến topic riêng cho cashier
        messagingTemplate.convertAndSend("/topic/invoices", payload);
    }
}
