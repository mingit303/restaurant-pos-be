package com.example.restaurant.service.admin;

import com.example.restaurant.domain.invoice.InvoiceStatus;
import com.example.restaurant.repository.invoice.InvoiceRepository;
import com.example.restaurant.repository.order.OrderItemRepository;
import com.example.restaurant.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {
    private final InvoiceRepository invoiceRepo;
    private final OrderItemRepository orderItemRepo;
    private final EmployeeRepository employeeRepo;

    public Map<String, Object> getRevenueSummary(String type, String date, Integer month, Integer year) {
        Map<String, Object> result = new HashMap<>();
        LocalDate now = LocalDate.now();

        if ("day".equals(type)) {
            LocalDate target = date != null ? LocalDate.parse(date) : now;
            var list = invoiceRepo.findByStatusAndPaidAtBetween(
                    InvoiceStatus.PAID,
                    target.atStartOfDay(),
                    target.plusDays(1).atStartOfDay()
            );
            result.put("title", "Doanh thu ngày " + target);
            fillSummary(result, list);
            return result;
        }

        if ("month".equals(type)) {
            int m = (month != null) ? month : now.getMonthValue();
            int y = (year != null) ? year : now.getYear();
            var list = invoiceRepo.findByMonthAndYear(InvoiceStatus.PAID, m, y);
            result.put("title", "Doanh thu tháng " + m + "/" + y);
            fillSummary(result, list);
            return result;
        }

        int y = (year != null) ? year : now.getYear();
        var list = invoiceRepo.findByYear(InvoiceStatus.PAID, y);
        result.put("title", "Doanh thu năm " + y);
        fillSummary(result, list);
        return result;
    }

    private void fillSummary(Map<String, Object> result, List<com.example.restaurant.domain.invoice.Invoice> list) {
        // 🧾 Tổng số hóa đơn
        result.put("invoiceCount", list.size());

        // 🧮 Tổng doanh thu trước VAT (subtotal - discount)
        BigDecimal totalBeforeVat = list.stream()
                .map(i -> {
                    BigDecimal subtotal = i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO;
                    BigDecimal discount = i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO;
                    return subtotal.subtract(discount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 💸 Tổng VAT
        BigDecimal totalVat = list.stream()
                .map(i -> i.getVatAmount() != null ? i.getVatAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 💰 Doanh thu sau VAT
        BigDecimal totalAfterVat = totalBeforeVat.add(totalVat);

        // 🪙 Tổng giảm giá
        BigDecimal totalDiscount = list.stream()
                .map(i -> i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 💵 Tiền mặt (CASH)
        BigDecimal cashBeforeVat = list.stream()
                .filter(i -> i.getPaymentMethod() != null && i.getPaymentMethod().name().equals("CASH"))
                .map(i -> {
                    BigDecimal subtotal = i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO;
                    BigDecimal discount = i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO;
                    return subtotal.subtract(discount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashVat = list.stream()
                .filter(i -> i.getPaymentMethod() != null && i.getPaymentMethod().name().equals("CASH"))
                .map(i -> i.getVatAmount() != null ? i.getVatAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 💳 VNPAY
        BigDecimal vnpBeforeVat = list.stream()
                .filter(i -> i.getPaymentMethod() != null && i.getPaymentMethod().name().equals("VNPAY"))
                .map(i -> {
                    BigDecimal subtotal = i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO;
                    BigDecimal discount = i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO;
                    return subtotal.subtract(discount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal vnpVat = list.stream()
                .filter(i -> i.getPaymentMethod() != null && i.getPaymentMethod().name().equals("VNPAY"))
                .map(i -> i.getVatAmount() != null ? i.getVatAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Ghi kết quả vào map
        result.put("totalBeforeVat", totalBeforeVat);
        result.put("totalVat", totalVat);
        result.put("totalAfterVat", totalAfterVat);
        result.put("totalDiscount", totalDiscount);

        result.put("cashBeforeVat", cashBeforeVat);
        result.put("cashVat", cashVat);
        result.put("cashTotal", cashBeforeVat.add(cashVat));

        result.put("vnpayBeforeVat", vnpBeforeVat);
        result.put("vnpayVat", vnpVat);
        result.put("vnpayTotal", vnpBeforeVat.add(vnpVat));
    }


    public List<Map<String, Object>> getTopMenuItems(Integer month, Integer year) {
        return orderItemRepo.findTopMenuItems(month, year);
    }

    public Map<String, List<Map<String, Object>>> getEmployeePerformance(Integer month, Integer year) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("waiters", employeeRepo.findWaiterPerformance(month, year));
        result.put("kitchens", employeeRepo.findKitchenPerformance(month, year));
        result.put("cashiers", employeeRepo.findCashierPerformance(month, year));
        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCashierPerformance(Integer month, Integer year) {
        return employeeRepo.findCashierPerformance(month, year);
    }

    public Integer getItemsSoldToday() {
        return orderItemRepo.countItemsSoldToday() != null ? orderItemRepo.countItemsSoldToday() : 0;
    }
    @Transactional(readOnly = true)
    public String getTopWaiterOfMonth() {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        var waiters = employeeRepo.findWaiterPerformance(month, year);
        if (waiters.isEmpty()) return null;
        return (String) waiters.get(0).get("name");
    }

    public Map<String, List<Map<String, Object>>> getMenuRanking(Integer month, Integer year) {
        Map<String, List<Map<String, Object>>> res = new HashMap<>();
        res.put("topSelling", orderItemRepo.findTopSelling(month, year).stream().limit(10).toList());
        res.put("leastSelling", orderItemRepo.findLeastSelling(month, year).stream().limit(10).toList());
        return res;
    }
}
