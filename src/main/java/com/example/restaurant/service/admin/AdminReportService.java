package com.example.restaurant.service.admin;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static BaseFont BASE_UNI;
    private static final Font F_TITLE, F_BOLD, F_NORMAL, F_SMALL;

    static {
        Font fTitle = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font fBold = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font fNormal = new Font(Font.HELVETICA, 11);
        Font fSmall = new Font(Font.HELVETICA, 9);

        try {
            try {
                BASE_UNI = BaseFont.createFont("uploads/fonts/NotoSans-Regular.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e) {
                BASE_UNI = BaseFont.createFont("uploads/fonts/Roboto-Regular.ttf",
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
            fTitle = new Font(BASE_UNI, 18, Font.BOLD);
            fBold = new Font(BASE_UNI, 12, Font.BOLD);
            fNormal = new Font(BASE_UNI, 11);
            fSmall = new Font(BASE_UNI, 9);

        } catch (Exception ignored) {}

        F_TITLE = fTitle;
        F_BOLD = fBold;
        F_NORMAL = fNormal;
        F_SMALL = fSmall;
    }

    // ==============================
    // Formatting Helpers
    // ==============================
    private String fmtMoney(Object v) {
        if (v == null) return "—";
        try {
            BigDecimal b = (v instanceof BigDecimal)
                    ? (BigDecimal) v
                    : new BigDecimal(v.toString());
            return String.format("%,.0f đ", b);
        } catch (Exception e) {
            return v.toString();
        }
    }

    private String fmtInt(Object v) {
        if (v == null) return "0";
        try {
            return Integer.toString(new BigDecimal(v.toString()).intValue());
        } catch (Exception e) {
            return v.toString();
        }
    }

    private PdfPCell cell(String text, Font font, int align, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(align);
        c.setPadding(6);
        if (bg != null) c.setBackgroundColor(bg);
        return c;
    }

    private void addDivider(Document doc) throws Exception {
        Paragraph p = new Paragraph("────────────────────────────────────────────────────", F_SMALL);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(4f);
        p.setSpacingAfter(4f);
        doc.add(p);
    }

    // ==============================
    // HEADER
    // ==============================
    private void addHeader(Document doc, String title, String subtitle) throws Exception {

        try {
            Image logo = Image.getInstance("uploads/images/logo/logo.png");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_CENTER);
            doc.add(logo);
        } catch (Exception ignored) {}

        Paragraph h = new Paragraph("MIKADO SUSHI RESTAURANT", F_TITLE);
        h.setAlignment(Element.ALIGN_CENTER);
        h.setSpacingBefore(5f);
        h.setSpacingAfter(4f);
        doc.add(h);

        Paragraph info = new Paragraph(
                title + "\n" +
                        (subtitle != null ? subtitle : "") + "\n" +
                        "Ngày in: " + LocalDate.now().atStartOfDay().format(DTF),
                F_NORMAL
        );
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingAfter(14f);
        doc.add(info);

        addDivider(doc);
    }

    // ==============================
    // FOOTER
    // ==============================
    private void addFooter(Document doc) throws Exception {
        addDivider(doc);
        Paragraph p = new Paragraph("Báo cáo nội bộ", F_NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(6f);
        doc.add(p);
    }

    // ==============================
    // 1. Revenue Report (A4 dọc)
    // ==============================
    public byte[] generateRevenueReport(Map<String, Object> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, "BÁO CÁO DOANH THU", (String) data.get("title"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(14f);

            table.addCell(cell("Số hóa đơn", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtInt(data.get("invoiceCount")), F_NORMAL, Element.ALIGN_RIGHT, null));

            table.addCell(cell("Doanh thu trước thuế", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("totalBeforeVat")), F_NORMAL, Element.ALIGN_RIGHT, null));

            table.addCell(cell("VAT", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell(fmtMoney(data.get("totalVat")), F_NORMAL, Element.ALIGN_RIGHT, null));

            table.addCell(cell("Doanh thu sau thuế", F_BOLD, Element.ALIGN_LEFT, new Color(230,255,230)));
            table.addCell(cell(fmtMoney(data.get("totalAfterVat")), F_BOLD, Element.ALIGN_RIGHT, new Color(230,255,230)));

            table.addCell(cell("Giảm giá", F_NORMAL, Element.ALIGN_LEFT, null));
            table.addCell(cell("-" + fmtMoney(data.get("totalDiscount")), F_NORMAL, Element.ALIGN_RIGHT, null));

            table.addCell(cell("Tiền mặt", F_NORMAL, Element.ALIGN_LEFT, new Color(240,255,240)));
            table.addCell(cell(fmtMoney(data.get("cashTotal")), F_NORMAL, Element.ALIGN_RIGHT, new Color(240,255,240)));

            table.addCell(cell("VNPAY", F_NORMAL, Element.ALIGN_LEFT, new Color(235,245,255)));
            table.addCell(cell(fmtMoney(data.get("vnpayTotal")), F_NORMAL, Element.ALIGN_RIGHT, new Color(235,245,255)));

            doc.add(table);
            addFooter(doc);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi báo cáo doanh thu: " + e.getMessage());
        }
    }

    // ==============================
    // 2. Employee Section Helper
    // ==============================
    private void addEmployeeSection(
            Document doc,
            String title,
            List<Map<String,Object>> list,
            String[] keys,
            String[] headers,
            Color headerBg
    ) throws Exception {

        Paragraph h = new Paragraph("\n" + title, F_BOLD);
        h.setAlignment(Element.ALIGN_CENTER);
        h.setSpacingBefore(6f);
        h.setSpacingAfter(6f);
        doc.add(h);

        PdfPTable t = new PdfPTable(headers.length);
        t.setWidthPercentage(100);
        t.setHeaderRows(1);
        t.setSpacingBefore(6f);

        for (String hName : headers)
            t.addCell(cell(hName, F_BOLD, Element.ALIGN_CENTER, headerBg));

        if (list == null || list.isEmpty()) {
            PdfPCell empty = cell("Không có dữ liệu", F_NORMAL, Element.ALIGN_CENTER, null);
            empty.setColspan(headers.length);
            t.addCell(empty);
        } else {
            for (var row : list) {
                for (String key : keys) {
                    Object val = row.get(key);
                    boolean isNum = "metric".equals(key);
                    String text = isNum ? fmtInt(val) :
                            val == null ? "—" : val.toString();

                    t.addCell(cell(text, F_NORMAL,
                            isNum ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT,
                            null));
                }
            }
        }

        doc.add(t);
        addDivider(doc);
    }

    // ==============================
    // 3. Employee Report (A4 dọc)
    // ==============================
    public byte[] generateEmployeeReport(Map<String, List<Map<String, Object>>> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, "BÁO CÁO HIỆU SUẤT NHÂN VIÊN", "Thời điểm: " + LocalDate.now());

            if (data.containsKey("waiters"))
                addEmployeeSection(doc, "NHÂN VIÊN PHỤC VỤ", data.get("waiters"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Số order đã phục vụ"},
                        new Color(220,240,255));

            if (data.containsKey("kitchens"))
                addEmployeeSection(doc, "NHÂN VIÊN BẾP", data.get("kitchens"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Món hoàn thành"},
                        new Color(255,248,220));

            if (data.containsKey("cashiers"))
                addEmployeeSection(doc, "THU NGÂN", data.get("cashiers"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Số hóa đơn"},
                        new Color(230,255,230));

            addFooter(doc);
            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi báo cáo nhân viên: " + e.getMessage());
        }
    }
    // ==============================
    // 4. Top Menu Report (A4 dọc)
    // ==============================
    public byte[] generateTopMenuReport(List<Map<String, Object>> items) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, "BÁO CÁO MÓN BÁN CHẠY", "Thời điểm: " + LocalDate.now());

            PdfPTable t = new PdfPTable(new float[]{4, 2, 3});
            t.setWidthPercentage(100);
            t.setHeaderRows(1);
            t.setSpacingBefore(14f);

            t.addCell(cell("Tên món", F_BOLD, Element.ALIGN_CENTER, new Color(230,230,230)));
            t.addCell(cell("Số lượng bán", F_BOLD, Element.ALIGN_CENTER, new Color(230,230,230)));
            t.addCell(cell("Doanh thu", F_BOLD, Element.ALIGN_CENTER, new Color(230,230,230)));

            for (var row : items) {
                t.addCell(cell((String) row.get("name"), F_NORMAL, Element.ALIGN_LEFT, null));
                t.addCell(cell(fmtInt(row.get("quantitySold")), F_NORMAL, Element.ALIGN_RIGHT, null));
                t.addCell(cell(fmtMoney(row.get("totalRevenue")), F_NORMAL, Element.ALIGN_RIGHT, null));
            }

            doc.add(t);
            addFooter(doc);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi báo cáo top món: " + e.getMessage());
        }
    }

    // ==============================
    // 5. Menu Ranking Report (A4 dọc)
    // ==============================
    public byte[] generateMenuRankingReport(Map<String, List<Map<String, Object>>> data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, "BÁO CÁO XẾP HẠNG MÓN ĂN", "Thời điểm: " + LocalDate.now());

            addMenuSection(doc, "TOP 10 MÓN BÁN CHẠY", data.get("topSelling"), new Color(235,255,235));
            addMenuSection(doc, "TOP 10 MÓN BÁN CHẬM", data.get("leastSelling"), new Color(255,235,235));

            addFooter(doc);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi báo cáo xếp hạng món: " + e.getMessage());
        }
    }

    // ==============================
    // 5b. Menu Section Helper
    // ==============================
    private void addMenuSection(Document doc, String title, List<Map<String, Object>> items, Color bg) throws Exception {

        Paragraph h = new Paragraph("\n" + title, F_BOLD);
        h.setAlignment(Element.ALIGN_CENTER);
        h.setSpacingBefore(6f);
        h.setSpacingAfter(6f);
        doc.add(h);

        PdfPTable t = new PdfPTable(new float[]{4, 2, 3});
        t.setWidthPercentage(100);
        t.setHeaderRows(1);
        t.setSpacingBefore(6f);

        t.addCell(cell("Tên món", F_BOLD, Element.ALIGN_CENTER, bg));
        t.addCell(cell("Số lượng", F_BOLD, Element.ALIGN_CENTER, bg));
        t.addCell(cell("Doanh thu", F_BOLD, Element.ALIGN_CENTER, bg));

        if (items == null || items.isEmpty()) {
            PdfPCell empty = cell("Không có dữ liệu", F_NORMAL, Element.ALIGN_CENTER, null);
            empty.setColspan(3);
            t.addCell(empty);
        } else {
            for (var i : items) {
                t.addCell(cell((String) i.get("name"), F_NORMAL, Element.ALIGN_LEFT, null));
                t.addCell(cell(fmtInt(i.get("quantitySold")), F_NORMAL, Element.ALIGN_RIGHT, null));
                t.addCell(cell(fmtMoney(i.get("totalRevenue")), F_NORMAL, Element.ALIGN_RIGHT, null));
            }
        }

        doc.add(t);
        addDivider(doc);
    }

    // ==============================
    // 6. Employee Report With Custom Title (A4 dọc)
    // ==============================
    public byte[] generateEmployeeReportWithTitle(
            Map<String, List<Map<String, Object>>> data,
            String customTitle
    ) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            addHeader(doc, customTitle, "Thời điểm: " + LocalDate.now());

            if (data.containsKey("waiters"))
                addEmployeeSection(doc, "NHÂN VIÊN PHỤC VỤ", data.get("waiters"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Số order đã phục vụ"},
                        new Color(220,240,255));

            if (data.containsKey("kitchens"))
                addEmployeeSection(doc, "NHÂN VIÊN BẾP", data.get("kitchens"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Món hoàn thành"},
                        new Color(255,248,220));

            if (data.containsKey("cashiers"))
                addEmployeeSection(doc, "THU NGÂN", data.get("cashiers"),
                        new String[]{"name", "metric"},
                        new String[]{"Tên", "Số hóa đơn"},
                        new Color(230,255,230));

            addFooter(doc);
            doc.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo PDF nhóm nhân viên: " + e.getMessage());
        }
    }
}

