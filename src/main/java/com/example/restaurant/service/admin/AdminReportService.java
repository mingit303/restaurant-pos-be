package com.example.restaurant.service.admin;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {

    // ====== Font setup (Unicode) ======
    private static BaseFont BASE_UNI;
    private static final Font F_TITLE, F_BOLD, F_NORMAL, F_SMALL;

    static {
        Font fTitle = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font fBold = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font fNormal = new Font(Font.HELVETICA, 11);
        Font fSmall = new Font(Font.HELVETICA, 9);
        try {
            // Ưu tiên dùng NotoSans, fallback Roboto
            try {
                BASE_UNI = BaseFont.createFont("uploads/fonts/NotoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e) {
                BASE_UNI = BaseFont.createFont("uploads/fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
            fTitle = new Font(BASE_UNI, 18, Font.BOLD);
            fBold = new Font(BASE_UNI, 12, Font.BOLD);
            fNormal = new Font(BASE_UNI, 11);
            fSmall = new Font(BASE_UNI, 9);
        } catch (Exception e) {
            System.err.println("⚠️ Không thể tải font Unicode, fallback Helvetica.");
        }
        F_TITLE = fTitle;
        F_BOLD = fBold;
        F_NORMAL = fNormal;
        F_SMALL = fSmall;
    }

    // ====== Helper định dạng ======
    private String fmtMoney(Object v) {
        if (v == null) return "—";
        try {
            BigDecimal b = (v instanceof BigDecimal) ? (BigDecimal) v : new BigDecimal(v.toString());
            return String.format("%,.0f đ", b);
        } catch (Exception e) {
            return v.toString();
        }
    }

    private String fmtInt(Object v) {
        if (v == null) return "0";
        try {
            return String.valueOf(new BigDecimal(v.toString()).intValue());
        } catch (Exception e) {
            return v.toString();
        }
    }

    private PdfPCell cell(String text, Font f, int align, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(align);
        if (bg != null) c.setBackgroundColor(bg);
        c.setPadding(6);
        return c;
    }

    private void addFooter(Document doc) throws Exception {
        doc.add(new Paragraph("\n──────────────────────────────────────────────", F_SMALL));
        Paragraph p = new Paragraph("Cảm ơn quý khách và hẹn gặp lại! 🍣", F_NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private void addHeader(Document doc, String title, String subtitle) throws Exception {
        try {
            Image logo = Image.getInstance("uploads/images/logo/logo.png");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception ignored) {}

        Paragraph h = new Paragraph("🍣 MIKADO SUSHI RESTAURANT 🍣", F_TITLE);
        h.setAlignment(Element.ALIGN_CENTER);
        doc.add(h);

        Paragraph sub = new Paragraph(title + "\n" + (subtitle != null ? subtitle : "") + "\nNgày in: " + LocalDate.now(), F_NORMAL);
        sub.setAlignment(Element.ALIGN_CENTER);
        doc.add(sub);
        doc.add(new Paragraph("──────────────────────────────────────────────", F_SMALL));
    }

    // ====== 1. Báo cáo Doanh thu ======
    public byte[] generateRevenueReport(Map<String, Object> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            addHeader(doc, "BÁO CÁO DOANH THU", (String) data.get("title"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(cell("Số hóa đơn", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtInt(data.get("invoiceCount")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("Doanh thu trước thuế", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("totalBeforeVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("Thuế VAT", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("totalVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("Doanh thu sau thuế", F_NORMAL, Element.ALIGN_LEFT, new Color(230, 255, 230)));
            table.addCell(cell(fmtMoney(data.get("totalAfterVat")), F_NORMAL, Element.ALIGN_RIGHT, new Color(230, 255, 230)));
            table.addCell(cell("Giảm giá", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell("-" + fmtMoney(data.get("totalDiscount")), F_NORMAL, Element.ALIGN_RIGHT, null));
           //  Tiền mặt (chi tiết)
            table.addCell(cell("Tiền mặt - Trước thuế", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("cashBeforeVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("Tiền mặt - VAT", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("cashVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("Tiền mặt - Sau thuế", F_NORMAL, Element.ALIGN_LEFT, new Color(240,255,240)));
            table.addCell(cell(fmtMoney(data.get("cashTotal")), F_NORMAL, Element.ALIGN_RIGHT, new Color(240,255,240)));

            //  VNPAY (chi tiết)
            table.addCell(cell("VNPAY - Trước thuế", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("vnpayBeforeVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("VNPAY - VAT", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("vnpayVat")), F_NORMAL, Element.ALIGN_RIGHT, null));
            table.addCell(cell("VNPAY - Sau thuế", F_NORMAL, Element.ALIGN_LEFT, new Color(230,245,255)));
            table.addCell(cell(fmtMoney(data.get("vnpayTotal")), F_NORMAL, Element.ALIGN_RIGHT, new Color(230,245,255)));

            doc.add(table);
            addFooter(doc);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ====== 2. Báo cáo Món bán chạy ======
    public byte[] generateTopMenuReport(List<Map<String, Object>> items) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            addHeader(doc, "BÁO CÁO MÓN BÁN CHẠY", "Thời điểm: " + LocalDate.now());

            PdfPTable t = new PdfPTable(new float[]{4, 2, 3});
            t.setWidthPercentage(100);
            t.setHeaderRows(1);
            t.addCell(cell("Tên món", F_BOLD, Element.ALIGN_CENTER, new Color(230, 230, 230)));
            t.addCell(cell("Số lượng bán", F_BOLD, Element.ALIGN_CENTER, new Color(230, 230, 230)));
            t.addCell(cell("Doanh thu (đ)", F_BOLD, Element.ALIGN_CENTER, new Color(230, 230, 230)));

            for (var i : items) {
                t.addCell(cell((String) i.get("name"), F_NORMAL, Element.ALIGN_LEFT, null));
                t.addCell(cell(fmtInt(i.get("quantitySold")), F_NORMAL, Element.ALIGN_RIGHT, null));
                t.addCell(cell(fmtMoney(i.get("totalRevenue")), F_NORMAL, Element.ALIGN_RIGHT, null));
            }

            doc.add(t);
            addFooter(doc);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ====== 3. Báo cáo Nhân viên (chia riêng từng nhóm) ======
    public byte[] generateEmployeeReport(Map<String, List<Map<String, Object>>> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            addHeader(doc, "BÁO CÁO HIỆU SUẤT NHÂN VIÊN", "Thời điểm: " + LocalDate.now());

            addEmployeeSection(doc, "🍽️ NHÂN VIÊN PHỤC VỤ", data.get("waiters"), new String[]{"name", "metric"}, new String[]{"Tên", "Số order đã phục vụ"}, new Color(220, 240, 255));
            addEmployeeSection(doc, "👨‍🍳 NHÂN VIÊN BẾP", data.get("kitchens"), new String[]{"name", "metric"}, new String[]{"Tên", "Món hoàn thành"}, new Color(255, 248, 220));
            addEmployeeSection(doc, "💵 THU NGÂN", data.get("cashiers"), new String[]{"name", "metric"}, new String[]{"Tên", "Số hóa đơn"}, new Color(230, 255, 230));

            addFooter(doc);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addEmployeeSection(Document doc, String title, List<Map<String, Object>> list, String[] keys, String[] headers, Color headerBg) throws Exception {
        Paragraph h = new Paragraph("\n" + title, new Font(F_BOLD));
        h.setAlignment(Element.ALIGN_CENTER);
        doc.add(h);
        doc.add(new Paragraph(" ", F_SMALL));

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        for (String c : headers) {
            table.addCell(cell(c, F_BOLD, Element.ALIGN_CENTER, headerBg));
        }

        if (list != null && !list.isEmpty()) {
            for (var row : list) {
                for (String key : keys) {
                    Object val = row.get(key);
                    boolean isMoney = "revenue".equals(key);
                    boolean isNumber = "metric".equals(key) || isMoney;
                    String text = isMoney ? fmtMoney(val) : (isNumber ? fmtInt(val) : (val == null ? "—" : val.toString()));
                    int align = isNumber ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT;
                    table.addCell(cell(text, F_NORMAL, align, null));
                }
            }
        } else {
            PdfPCell empty = cell("Không có dữ liệu", F_NORMAL, Element.ALIGN_CENTER, null);
            empty.setColspan(headers.length);
            table.addCell(empty);
        }

        doc.add(table);
        doc.add(new Paragraph("───────────────────────────────────────────────────────────────", F_SMALL));
    }

    public byte[] generateEmployeeReportWithTitle(Map<String, List<Map<String, Object>>> data, String customTitle) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, customTitle, "Thời điểm: " + LocalDate.now());

            if (data.containsKey("waiters"))
                addEmployeeSection(doc, "🍽️ NHÂN VIÊN PHỤC VỤ", data.get("waiters"),
                    new String[]{"name", "metric"}, new String[]{"Tên", "Số order đã phục vụ"}, new Color(220, 240, 255));

            if (data.containsKey("kitchens"))
                addEmployeeSection(doc, "👨‍🍳 NHÂN VIÊN BẾP", data.get("kitchens"),
                    new String[]{"name", "metric"}, new String[]{"Tên", "Món hoàn thành"}, new Color(255, 248, 220));

            if (data.containsKey("cashiers"))
                addEmployeeSection(doc, "💵 THU NGÂN", data.get("cashiers"),
                    new String[]{"name", "metric"}, new String[]{"Tên", "Số hóa đơn"}, new Color(230, 255, 230));

            addFooter(doc);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateMenuRankingReport(Map<String, List<Map<String, Object>>> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 40, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, "BÁO CÁO XẾP HẠNG MÓN ĂN", "Thời điểm: " + LocalDate.now());

            addMenuSection(doc, "🔥 TOP 10 MÓN BÁN CHẠY NHẤT", data.get("topSelling"), new Color(230,255,230));
            addMenuSection(doc, "🥶 TOP 10 MÓN BÁN CHẬM NHẤT", data.get("leastSelling"), new Color(255,230,230));

            addFooter(doc);
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addMenuSection(Document doc, String title, List<Map<String,Object>> items, Color headerColor) throws Exception {
        Paragraph h = new Paragraph("\n" + title, F_BOLD);
        h.setAlignment(Element.ALIGN_CENTER);
        doc.add(h);

        PdfPTable t = new PdfPTable(new float[]{4,2,3});
        t.setWidthPercentage(100);
        t.setHeaderRows(1);
        t.addCell(cell("Tên món", F_BOLD, Element.ALIGN_CENTER, headerColor));
        t.addCell(cell("Số lượng bán", F_BOLD, Element.ALIGN_CENTER, headerColor));
        t.addCell(cell("Doanh thu (đ)", F_BOLD, Element.ALIGN_CENTER, headerColor));

        if (items != null && !items.isEmpty()) {
            for (var i : items) {
                t.addCell(cell((String) i.get("name"), F_NORMAL, Element.ALIGN_LEFT, null));
                t.addCell(cell(fmtInt(i.get("quantitySold")), F_NORMAL, Element.ALIGN_RIGHT, null));
                t.addCell(cell(fmtMoney(i.get("totalRevenue")), F_NORMAL, Element.ALIGN_RIGHT, null));
            }
        } else {
            PdfPCell c = cell("Không có dữ liệu", F_NORMAL, Element.ALIGN_CENTER, null);
            c.setColspan(3);
            t.addCell(c);
        }

        doc.add(t);
    }

}
