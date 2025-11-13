package com.example.restaurant.controller.admin;

import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.invoice.InvoiceRepository;
import com.example.restaurant.repository.order.OrderItemRepository;
import com.example.restaurant.service.admin.AdminReportService;
import com.example.restaurant.service.admin.AdminStatisticsService;
// import com.example.restaurant.service.invoice.InvoiceService;
// import com.example.restaurant.service.order.OrderService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService statsService;
    private final AdminReportService pdfService;
    private final InvoiceRepository invoiceRepo;
    private final OrderItemRepository orderItemRepo;
    private final EmployeeRepository employeeRepository;


    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestParam String type,
                                        @RequestParam(required = false) String date,
                                        @RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statsService.getRevenueSummary(type, date, month, year));
    }

    @GetMapping("/revenue/pdf")
    public ResponseEntity<byte[]> exportRevenuePDF(@RequestParam String type,
                                                   @RequestParam(required = false) String date,
                                                   @RequestParam(required = false) Integer month,
                                                   @RequestParam(required = false) Integer year) {
        byte[] pdf = pdfService.generateRevenueReport(statsService.getRevenueSummary(type, date, month, year));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=revenue_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/menu/top")
    public ResponseEntity<?> getTopMenu(@RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statsService.getTopMenuItems(month, year));
    }

    @GetMapping("/menu/top/pdf")
    public ResponseEntity<byte[]> exportTopMenuPDF(@RequestParam(required = false) Integer month,
                                                   @RequestParam(required = false) Integer year) {
        byte[] pdf = pdfService.generateTopMenuReport(statsService.getTopMenuItems(month, year));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=top_menu_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getEmployeeStats(@RequestParam(required = false) Integer month,
                                              @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(statsService.getEmployeePerformance(month, year));
    }

    @GetMapping("/employees/pdf")
    public ResponseEntity<byte[]> exportEmployeesPDF(@RequestParam(required = false) Integer month,
                                                     @RequestParam(required = false) Integer year) {
        byte[] pdf = pdfService.generateEmployeeReport(statsService.getEmployeePerformance(month, year));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=employee_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/cashier-performance")
    public ResponseEntity<List<Map<String, Object>>> getCashierPerformance(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(statsService.getCashierPerformance(month, year));
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardKPI() {
        Map<String, Object> res = new HashMap<>();
        res.put("totalRevenue", invoiceRepo.sumRevenueToday());
        res.put("totalItemsSold", orderItemRepo.countItemsSoldToday());
        res.put("topEmployee", statsService.getTopWaiterOfMonth());
        return res;
    }

    @GetMapping("/export/employee/{group}")
    public ResponseEntity<byte[]> exportEmployeeGroupPDF(
            @PathVariable String group,
            @RequestParam int month,
            @RequestParam int year) {

        Map<String, List<Map<String, Object>>> data = new HashMap<>();

        switch (group.toLowerCase()) {
            case "waiter" -> data.put("waiters", employeeRepository.findWaiterPerformance(month, year));
            case "kitchen" -> data.put("kitchens", employeeRepository.findKitchenPerformance(month, year));
            case "cashier" -> data.put("cashiers", employeeRepository.findCashierPerformance(month, year));
            default -> throw new IllegalArgumentException("Invalid group: " + group);
        }

        String title;
        switch (group.toLowerCase()) {
            case "waiter" -> title = "BÁO CÁO HIỆU SUẤT NHÂN VIÊN PHỤC VỤ";
            case "kitchen" -> title = "BÁO CÁO HIỆU SUẤT NHÂN VIÊN BẾP";
            case "cashier" -> title = "BÁO CÁO HIỆU SUẤT THU NGÂN";
            default -> title = "BÁO CÁO HIỆU SUẤT NHÂN VIÊN";
        }

        byte[] pdf = pdfService.generateEmployeeReportWithTitle(data, title);

        String filename = "employee_" + group + "_" + month + "_" + year + ".pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/menu/ranking")
    public ResponseEntity<?> getMenuRanking(@RequestParam Integer month, @RequestParam Integer year) {
        return ResponseEntity.ok(statsService.getMenuRanking(month, year));
    }

    @GetMapping("/menu/ranking/pdf")
    public ResponseEntity<byte[]> exportMenuRankingPDF(@RequestParam Integer month, @RequestParam Integer year) {
        byte[] pdf = pdfService.generateMenuRankingReport(statsService.getMenuRanking(month, year));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=menu_ranking.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

}
