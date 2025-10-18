package com.example.restaurant.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {
    // private static final Locale VN = Locale.forLanguageTag("vi-VN");
    // public static String format(BigDecimal amount) {
    //     if (amount == null) return "0 ₫";
    //     NumberFormat nf = NumberFormat.getCurrencyInstance(VN);
    //     nf.setMaximumFractionDigits(0);
    //     return nf.format(amount);
    // }
    private static final NumberFormat VN_FORMAT =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    public static String format(BigDecimal value) {
        if (value == null) return "0 ₫";
        return VN_FORMAT.format(value) + " ₫";
    }

    // private static final Locale VNM = new Locale("vi", "VN");
    // public static String format(BigDecimal amount) {
    //     if (amount == null) return "0 ₫";
    //     NumberFormat nf = NumberFormat.getInstance(VNM);
    //     return nf.format(amount) + " ₫";
    // }
}