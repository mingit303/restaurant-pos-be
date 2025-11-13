package com.example.restaurant.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {

    private static final Locale VN = Locale.forLanguageTag("vi-VN");

    public static String format(BigDecimal value) {
        if (value == null) return "0 ₫";

        NumberFormat nf = NumberFormat.getInstance(VN);
        nf.setMaximumFractionDigits(0);

        return nf.format(value) + " ₫";
    }

}
