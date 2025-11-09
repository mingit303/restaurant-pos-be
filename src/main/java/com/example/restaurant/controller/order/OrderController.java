package com.example.restaurant.controller.order;

import com.example.restaurant.domain.order.OrderItemState;
import com.example.restaurant.dto.order.request.*;
import com.example.restaurant.dto.order.response.OrderResponse;
import com.example.restaurant.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController @RequestMapping("/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req){
        return ResponseEntity.ok(service.createOrder(req));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItem(@PathVariable Long orderId, @Valid @RequestBody AddItemRequest req){
        return ResponseEntity.ok(service.addItem(orderId, req));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(required=false) String tableCode,
            @RequestParam(required=false) String waiterName,
            @RequestParam(required=false) String status,
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ){
        return ResponseEntity.ok(service.search(page, size, tableCode, waiterName, status, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> detail(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/current/{tableId}")
    public ResponseEntity<?> getCurrentOrder(@PathVariable Long tableId) {
        var res = service.getCurrentByTable(tableId);
        return (res != null) ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirm(@PathVariable Long id){
        return ResponseEntity.ok(service.confirmOrder(id));
    }

    @PatchMapping("/items/{id}/state")
    public ResponseEntity<Void> updateItemState(@PathVariable Long id, @Valid @RequestBody UpdateItemStateRequest req){
        service.updateItemState(id, OrderItemState.valueOf(req.getState()));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/items/{id}/served")
    public ResponseEntity<OrderResponse> markServed(@PathVariable Long id) {
        return ResponseEntity.ok(service.markItemServed(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/change-table")
    public ResponseEntity<OrderResponse> changeTable(
            @PathVariable Long id,
            @Valid @RequestBody ChangeTableRequest req) {
        return ResponseEntity.ok(service.changeTable(id, req.getNewTableId()));
    }

}
