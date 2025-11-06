package com.example.restaurant.controller.invoice;

import com.example.restaurant.dto.invoice.request.CreateInvoiceRequest;
import com.example.restaurant.dto.invoice.response.InvoiceResponse;
import com.example.restaurant.service.invoice.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService service;

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAll(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.getAll(status));
    }


    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody CreateInvoiceRequest req){
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}/confirm-cash")
    public ResponseEntity<InvoiceResponse> confirmCash(@PathVariable Long id){
        return ResponseEntity.ok(service.confirmCash(id));
    }   

    @GetMapping("/vnpay-return")
    public RedirectView vnpayReturn(@RequestParam Map<String, String> params) {
        // Gọi service xử lý xác thực checksum và cập nhật trạng thái hóa đơn
        String result = service.handleVnpayReturn(params);

        // Lấy dữ liệu cần hiển thị
        String amount = params.get("vnp_Amount");
        String orderCode = params.get("vnp_TxnRef");
        String payDate = params.get("vnp_PayDate");

        // Redirect về frontend cùng với query params để hiển thị
        String redirectUrl = String.format(
            "http://localhost:5174/payment-success?status=%s&amount=%s&orderCode=%s&payDate=%s",
            "success".equals(result) ? "success" : "fail",
            amount,
            orderCode,
            payDate
        );

        return new RedirectView(redirectUrl);
    }



    @PostMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleVnpayIpn(@RequestParam Map<String, String> params) {
        String res = service.handleVnpayIpn(params);
        return ResponseEntity.ok(Map.of("RspCode", res, "Message", res.equals("00") ? "Confirm Success" : "Failed"));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable Long id) {
        byte[] pdf = service.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=invoice-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return switch (type.toLowerCase()) {
            case "day" -> ResponseEntity.ok(service.getSummaryByDate(LocalDate.parse(date)));
            case "month" -> ResponseEntity.ok(service.getSummaryByMonth(month, year));
            case "year" -> ResponseEntity.ok(service.getSummaryByYear(year));
            default -> ResponseEntity.badRequest().build();
        };
    }
}