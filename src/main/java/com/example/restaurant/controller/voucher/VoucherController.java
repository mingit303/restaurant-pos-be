package com.example.restaurant.controller.voucher;

import com.example.restaurant.dto.voucher.request.VoucherRequest;
import com.example.restaurant.dto.voucher.response.VoucherCheckResponse;
import com.example.restaurant.dto.voucher.response.VoucherResponse;
import com.example.restaurant.service.voucher.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService service;

    @GetMapping
    public ResponseEntity<Page<VoucherResponse>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Boolean active,
            @RequestParam(required=false) LocalDate from,
            @RequestParam(required=false) LocalDate to
    ){
        return ResponseEntity.ok(service.search(page, size, keyword, active, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> detail(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<VoucherResponse> create(@Valid @RequestBody VoucherRequest req){
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherResponse> update(@PathVariable Long id, @Valid @RequestBody VoucherRequest req){
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cashier/Waiter gọi trước khi áp dụng mã 
    @GetMapping("/check")
    public ResponseEntity<VoucherCheckResponse> check(@RequestParam String code){
        return ResponseEntity.ok(service.check(code));
    }

    @GetMapping("/usable")
    public ResponseEntity<List<VoucherCheckResponse>> usable() {
        return ResponseEntity.ok(service.getUsableVouchers());
    }

}
