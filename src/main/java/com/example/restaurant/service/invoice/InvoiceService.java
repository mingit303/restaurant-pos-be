package com.example.restaurant.service.invoice;

import com.example.restaurant.domain.invoice.*;
import com.example.restaurant.domain.order.*;
import com.example.restaurant.domain.table.*;
import com.example.restaurant.domain.voucher.Voucher;
import com.example.restaurant.dto.invoice.request.CreateInvoiceRequest;
import com.example.restaurant.dto.invoice.response.InvoiceResponse;
import com.example.restaurant.exception.BadRequestException;
import com.example.restaurant.exception.NotFoundException;
import com.example.restaurant.repository.employee.EmployeeRepository;
import com.example.restaurant.repository.invoice.InvoiceRepository;
import com.example.restaurant.repository.order.OrderRepository;
import com.example.restaurant.repository.table.RestaurantTableRepository;
import com.example.restaurant.repository.voucher.VoucherRepository;
import com.example.restaurant.service.voucher.VoucherService;
import com.example.restaurant.ws.OrderEventPublisher;
import com.example.restaurant.ws.TableEventPublisher;
import com.example.restaurant.domain.customer.Customer;
import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.service.customer.CustomerService;
import com.example.restaurant.util.MoneyUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.restaurant.ws.InvoiceEventPublisher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceEventPublisher invoiceEvents;
    private final InvoiceRepository invoiceRepo;
    private final EmployeeRepository employeeRepo; 
    private final OrderRepository orderRepo;
    private final VoucherRepository voucherRepo;
    private final VoucherService voucherService;
    private final RestaurantTableRepository tableRepo;
    private final TableEventPublisher tableEvents;
    private final OrderEventPublisher orderEvents;
    private final CustomerService customerService;

    @Value("${vnpay.tmn-code}") private String vnp_TmnCode;
    @Value("${vnpay.hash-secret}") private String vnp_HashSecret;
    @Value("${vnpay.pay-url}") private String vnp_PayUrl;
    @Value("${vnpay.return-url}") private String vnp_ReturnUrl;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAll(String status) {
        List<Invoice> list;
        if (status != null && !status.isBlank()) {
            InvoiceStatus s = InvoiceStatus.valueOf(status.toUpperCase());
            list = invoiceRepo.findAll().stream()
                    .filter(i -> i.getStatus() == s)
                    .toList();
        } else {
            list = invoiceRepo.findAll();
        }
        return list.stream().map(this::map).toList();
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest req) {
        Order order = orderRepo.findById(req.getOrderId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order."));
        if (order.getStatus() != OrderStatus.SERVED)
            throw new BadRequestException("Order chưa sẵn sàng thanh toán.");

        PaymentMethod method = PaymentMethod.valueOf(req.getPaymentMethod().toUpperCase());
        Invoice invoice = Invoice.builder()
                .order(order)
                .paymentMethod(method)
                .status(InvoiceStatus.UNPAID)
                .subtotal(order.getTotal())
                .createdAt(LocalDateTime.now())
                .build();
        invoice.setTotal(invoice.getSubtotal());

        Customer customer = null;
        if (req.getCustomerPhone() != null && !req.getCustomerPhone().isBlank()) {
            customer = customerService.getOrCreateCustomer(req.getCustomerPhone(), req.getCustomerName());
            invoice.setCustomer(customer);
        }

        // Áp dụng voucher trước
        if (req.getVoucherCode() != null && !req.getVoucherCode().isBlank()) {
            Voucher v = voucherRepo.findByCodeIgnoreCase(req.getVoucherCode())
                    .orElseThrow(() -> new NotFoundException("Voucher không tồn tại."));
            String msg = getVoucherError(v);
            if (msg != null) throw new BadRequestException(msg);

            BigDecimal voucherDiscount = order.getTotal()
                    .multiply(v.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100));
            if (v.getMaxDiscount() != null && voucherDiscount.compareTo(v.getMaxDiscount()) > 0)
                voucherDiscount = v.getMaxDiscount();

            invoice.setDiscount(voucherDiscount);
            invoice.setVoucher(v);
        } else {
            invoice.setDiscount(BigDecimal.ZERO);
        }

        // Xử lý dùng điểm
        if (customer != null && req.getRedeemPoints() != null && req.getRedeemPoints() > 0) {
            int redeem = req.getRedeemPoints();
            BigDecimal redeemValue = BigDecimal.valueOf(redeem * 1000L); // 1 điểm = 1000đ

            // Giới hạn 50% giá trị sau voucher
            BigDecimal halfAfterVoucher = invoice.getSubtotal().subtract(invoice.getDiscount())
                    .multiply(BigDecimal.valueOf(0.5));
            if (redeemValue.compareTo(halfAfterVoucher) > 0)
                redeemValue = halfAfterVoucher;

            Customer managedCustomer = customerService.getById(customer.getId());
            customerService.redeemPoints(managedCustomer, redeem, "Dùng điểm đổi cho hóa đơn #" + order.getId());

            invoice.setRedeemedPoints(redeem);
            invoice.setRedeemedValue(redeemValue);
            invoice.setDiscount(invoice.getDiscount().add(redeemValue)); // Cộng thêm vào tổng discount
        }

        // Tính tổng sau VAT
        BigDecimal afterDiscount = invoice.getSubtotal().subtract(invoice.getDiscount());
        BigDecimal vatAmount = afterDiscount
        .multiply(invoice.getVatRate())
        .setScale(0, RoundingMode.DOWN);
        invoice.setVatAmount(vatAmount);
        invoice.setTotal(afterDiscount.add(vatAmount));


        // Nếu là VNPAY
        if (method == PaymentMethod.VNPAY) {
            String txnRef = "INV" + System.currentTimeMillis();
            invoice.setTransactionRef(txnRef);
            String payUrl = createVnpayUrl(invoice);
            invoiceRepo.save(invoice);
            invoiceEvents.invoiceChanged(invoice, "CREATED");
            return new InvoiceResponse(invoice.getId(), order.getId(), method, invoice.getStatus(),
                    invoice.getSubtotal(), 
                    invoice.getDiscount(), 
                    invoice.getTotal(),
                    invoice.getVoucher() != null ? invoice.getVoucher().getCode() : null,
                    invoice.getCreatedAt(), invoice.getPaidAt(), 
                    payUrl, 
                    invoice.getCustomer() != null ? invoice.getCustomer().getName() : null,
                    invoice.getCustomer() != null ? invoice.getCustomer().getPhone() : null,
                    invoice.getVatRate(),
                    invoice.getVatAmount(),
                    invoice.getOrder().getTable() != null ? invoice.getOrder().getTable().getCode() : null,
                    afterDiscount
                );
        }

        // Save trước rồi mới broadcast
        Invoice saved = invoiceRepo.save(invoice);
        invoiceEvents.invoiceChanged(saved, "CREATED");

        return map(saved);
    }


    // Tạo link redirect VNPAY 
   private String createVnpayUrl(Invoice invoice) {
    try {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", invoice.getTotal().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", invoice.getTransactionRef());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang #" + invoice.getOrder().getId());
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_BankCode", "NCB");

        // thời gian tạo + hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Build hashData và query
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = vnp_PayUrl + "?" + query;
        return paymentUrl;
    } catch (Exception e) {
        throw new RuntimeException("Không tạo được URL VNPAY: " + e.getMessage());
    }
}


    // Xác nhận thanh toán tiền mặt 
    @Transactional
    public InvoiceResponse confirmCash(Long id) {
        Invoice inv = invoiceRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn."));
        inv.setStatus(InvoiceStatus.PAID);
        inv.setPaidAt(LocalDateTime.now());

        if (inv.getPaymentMethod() == PaymentMethod.CASH) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee cashier = employeeRepo.findByUserUsername(username)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên thu ngân"));
            inv.setCashier(cashier);
        }
        invoiceRepo.save(inv);

        if (inv.getCustomer() != null) {
            Customer customer = customerService
                            .getById(inv.getCustomer().getId());
            int earn = inv.getTotal().divide(BigDecimal.valueOf(10000)).intValue();
            customerService.addPoints(customer, earn, "Tích điểm hóa đơn #" + inv.getId());
        }

        finishOrder(inv);
        invoiceEvents.invoiceChanged(inv, "PAID");
        return map(inv);
    }

    // Callback từ VNPAY 
    @Transactional
    public String handleVnpayReturn(Map<String, String> params) {
        String ref = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        Optional<Invoice> opt = invoiceRepo.findByTransactionRef(ref);
        if (opt.isEmpty()) return "error";
        Invoice inv = opt.get();

        if ("00".equals(responseCode)) {
            inv.setStatus(InvoiceStatus.PAID);
            inv.setTransactionNo(params.get("vnp_TransactionNo"));
            inv.setPaidAt(LocalDateTime.now());
            invoiceRepo.save(inv);

            // Cộng điểm thưởng cho khách hàng nếu có
            if (inv.getCustomer() != null) {
                // Nạp lại entity customer để chắc chắn là managed
                Customer customer = customerService.getById(inv.getCustomer().getId());
                int earn = inv.getTotal().divide(BigDecimal.valueOf(10000)).intValue();
                customerService.addPoints(customer, earn, "Tích điểm hóa đơn #" + inv.getId());
            }

            // Hoàn tất order & bàn
            finishOrder(inv);

            // Phát realtime event
            invoiceEvents.invoiceChanged(inv, "PAID");

            return "success";
        } else {
            inv.setStatus(InvoiceStatus.CANCELED);
            invoiceRepo.save(inv);
            return "failed";
        }
    }


    private void finishOrder(Invoice inv) {
        Order order = inv.getOrder();
        order.setStatus(OrderStatus.PAID);
        RestaurantTable table = order.getTable();
        if (table != null) {
            table.setStatus(TableStatus.CLEANING);
            tableRepo.save(table);
            tableEvents.tableChanged(table.getId(), table.getCode(), table.getCapacity(),
                    table.getStatus().name(), "STATUS_CHANGED");
        }
        orderRepo.save(order);
        orderEvents.orderChanged(order, "PAID");
        if (inv.getVoucher() != null)
            voucherService.increaseUsage(inv.getVoucher().getCode());
    }

    private InvoiceResponse map(Invoice i) {
        BigDecimal afterDiscount = i.getSubtotal()
        .subtract(i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO);
        return new InvoiceResponse(i.getId(), i.getOrder().getId(), i.getPaymentMethod(), i.getStatus(),
                i.getSubtotal(), i.getDiscount(), i.getTotal(),
                i.getVoucher() != null ? i.getVoucher().getCode() : null,
                i.getCreatedAt(), i.getPaidAt(), i.getTransactionRef(),
                i.getCustomer() != null ? i.getCustomer().getName() : null,
                i.getCustomer() != null ? i.getCustomer().getPhone() : null, i.getVatRate(),
                i.getVatAmount(), 
                i.getOrder().getTable() != null ? i.getOrder().getTable().getCode() : null,
                afterDiscount);
    }

    private String getVoucherError(Voucher v) {
        LocalDateTime now = LocalDateTime.now();
        if (!v.isActive()) return "Voucher không hoạt động.";
        if (v.getStartDate() != null && now.toLocalDate().isBefore(v.getStartDate())) return "Voucher chưa bắt đầu.";
        if (v.getEndDate() != null && now.toLocalDate().isAfter(v.getEndDate())) return "Voucher đã hết hạn.";
        return null;
    }

    private static String hmacSHA512(String key, String data) throws Exception {
        javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secret_key =
                new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secret_key);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Transactional
    public String handleVnpayIpn(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            params.remove("vnp_SecureHash");

            // build chuỗi hash
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            StringBuilder sb = new StringBuilder();
            for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
                String key = itr.next();
                String value = params.get(key);
                if (value != null && !value.isEmpty()) {
                    sb.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) sb.append('&');
                }
            }

            String signValue = hmacSHA512(vnp_HashSecret, sb.toString());
            if (!signValue.equals(vnp_SecureHash)) return "97"; // checksum sai

            String txnRef = params.get("vnp_TxnRef");
            String respCode = params.get("vnp_ResponseCode");
            Invoice inv = invoiceRepo.findByTransactionRef(txnRef).orElse(null);
            if (inv == null) return "01"; // order not found

            if ("00".equals(respCode)) {
                inv.setStatus(InvoiceStatus.PAID);
                inv.setPaidAt(LocalDateTime.now());
                invoiceRepo.save(inv);
                finishOrder(inv);
                return "00"; // Confirm Success
            } else {
                inv.setStatus(InvoiceStatus.CANCELED);
                invoiceRepo.save(inv);
                return "02"; // failed
            }
        } catch (Exception e) {
            return "99"; // unknown error
        }
    }


@Transactional(readOnly = true)
public byte[] generateInvoicePdf(Long id) {
    Invoice inv = invoiceRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn."));
    Order order = inv.getOrder();

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        Document doc = new Document(PageSize.A5, 25, 25, 20, 20);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        try {
            Image logo = Image.getInstance("uploads/images/logo/logo.png");
            logo.scaleToFit(90, 90);
            logo.setAlignment(Image.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception e) {
            System.out.println("Không thể load logo.");
        }

        /* ===================== FONT ===================== */
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 11);
        Font smallFont = new Font(Font.HELVETICA, 9);

        Paragraph header = new Paragraph("Mikado Sushi Restaurant", titleFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(header);

        Paragraph address = new Paragraph(
                "123 Đ. 3 Tháng 2, Xuân Khánh, Ninh Kiều, Cần Thơ\nHotline: 0912 345 678",
                smallFont
        );
        address.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(address);

        Paragraph devidedLine = new Paragraph("----------------------------------------------", smallFont);
        devidedLine.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(devidedLine);


        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", boldFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(title);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        doc.add(new Paragraph(
                "Mã hóa đơn: #" + inv.getId(),normalFont));
        Paragraph time = new Paragraph("Ngày: " + inv.getCreatedAt().format(dtf), normalFont);
        doc.add(time);
        // doc.add(new Paragraph(
        //         "Phương thức: " + inv.getPaymentMethod()
        //                 + "     Trạng thái: " + inv.getStatus(),
        //         normalFont
        // ));

        // if (inv.getCustomer() != null) {
        //     doc.add(new Paragraph(
        //             "Khách hàng: " + inv.getCustomer().getName()
        //                     + " (" + inv.getCustomer().getPhone() + ")",
        //             normalFont
        //     ));
        // }

        doc.add(devidedLine);

        //Gom nhóm các món
        Map<String, OrderItem> group = new LinkedHashMap<>();

        for (OrderItem item : order.getItems()) {
            String key = item.getMenuItem().getName() + "__" + (item.getNote() == null ? "" : item.getNote());

            if (!group.containsKey(key)) {
                group.put(key,
                    OrderItem.builder()
                        .id(null)
                        .order(null) 
                        .menuItem(item.getMenuItem())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .lineTotal(item.getLineTotal())
                        .note(item.getNote())
                        .state(item.getState())
                        .chef(item.getChef())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .doneAt(item.getDoneAt())
                        .build()
                );

            } else {
                OrderItem g = group.get(key);
                g.setQuantity(g.getQuantity() + item.getQuantity());
                g.setLineTotal(g.getLineTotal().add(item.getLineTotal()));
            }
        }

        PdfPTable table = new PdfPTable(new float[]{3, 1, 2, 2});
        table.setWidthPercentage(100);

        table.addCell("Món");
        table.addCell("SL");
        table.addCell("Đơn giá");
        table.addCell("Thành tiền");

        for (OrderItem item : group.values()) {
            table.addCell(item.getMenuItem().getName()
                    + (item.getNote() != null && !item.getNote().isBlank()
                    ? "\n(" + item.getNote() + ")"
                    : ""));
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(MoneyUtils.format(item.getMenuItem().getPrice()));
            table.addCell(MoneyUtils.format(item.getLineTotal()));
        }

        doc.add(table);
        doc.add(devidedLine);

        BigDecimal subtotal = inv.getSubtotal();
        BigDecimal discount = inv.getDiscount() != null ? inv.getDiscount() : BigDecimal.ZERO;
        BigDecimal vat = inv.getVatAmount() != null ? inv.getVatAmount() : BigDecimal.ZERO;
        BigDecimal total = inv.getTotal();

        doc.add(new Paragraph(String.format("TẠM TÍNH: %28s", MoneyUtils.format(subtotal)), normalFont));

        if (discount.compareTo(BigDecimal.ZERO) > 0)
            doc.add(new Paragraph(String.format("Giảm giá: %29s", MoneyUtils.format(discount)), normalFont));

        doc.add(new Paragraph(String.format("VAT: %34s", MoneyUtils.format(vat), normalFont)));

        doc.add(new Paragraph(String.format("TỔNG CỘNG: %23s", MoneyUtils.format(total)), boldFont));

        doc.add(devidedLine);

        Paragraph thanks = new Paragraph("Cảm ơn quý khách và hẹn gặp lại!", normalFont);
        thanks.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(thanks);

        doc.close();
        return baos.toByteArray();

    } catch (Exception e) {
        throw new RuntimeException("Lỗi xuất PDF: " + e.getMessage());
    }
}


    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Invoice> invoices = invoiceRepo.findByStatusAndPaidAtBetween(
            InvoiceStatus.PAID, start, end
        );

        return calculateSummary(invoices, "Ngày " + date);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryByMonth(int month, int year) {
        List<Invoice> invoices = invoiceRepo.findByMonthAndYear(InvoiceStatus.PAID, month, year);
        return calculateSummary(invoices, "Tháng " + month + "/" + year);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryByYear(int year) {
        List<Invoice> invoices = invoiceRepo.findByYear(InvoiceStatus.PAID, year);
        return calculateSummary(invoices, "Năm " + year);
    }

    // Hàm dùng chung 
    private Map<String, Object> calculateSummary(List<Invoice> invoices, String title) {
        BigDecimal totalRevenue = invoices.stream()
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = invoices.stream()
            .map(i -> i.getDiscount() != null ? i.getDiscount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVat = invoices.stream()
            .map(i -> i.getVatAmount() != null ? i.getVatAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashTotal = invoices.stream()
            .filter(i -> i.getPaymentMethod() == PaymentMethod.CASH)
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal vnpayTotal = invoices.stream()
            .filter(i -> i.getPaymentMethod() == PaymentMethod.VNPAY)
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("title", title);
        map.put("invoiceCount", invoices.size());
        map.put("totalRevenue", totalRevenue);
        map.put("totalDiscount", totalDiscount);
        map.put("totalVat", totalVat);
        map.put("cashTotal", cashTotal);
        map.put("vnpayTotal", vnpayTotal);
        return map;
    }

    @Transactional(readOnly = true)
    public BigDecimal getRevenue(LocalDateTime from, LocalDateTime to) {
        return invoiceRepo.sumTotalBetween(from, to);
    }

    @Transactional
    public String createVnpayPaymentLinkForCashier(Long id) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hóa đơn."));

        if (invoice.getPaymentMethod() != PaymentMethod.VNPAY)
            throw new BadRequestException("Hóa đơn này không phải thanh toán qua VNPAY.");

        // Nếu chưa có transactionRef thì tạo mới
        if (invoice.getTransactionRef() == null) {
            invoice.setTransactionRef("INV" + System.currentTimeMillis());
            invoiceRepo.save(invoice);
        }

        // Tạo lại link thanh toán
        return createVnpayUrl(invoice);
    }

}