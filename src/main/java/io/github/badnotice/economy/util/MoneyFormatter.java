package io.github.badnotice.economy.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyFormatter {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    private static final Pattern PATTERN;
    private static final DecimalFormat DECIMAL_FORMAT;

    private static final List<String> SUFFIXES;

    static {
        PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");
        DECIMAL_FORMAT = new DecimalFormat("#.##");

        SUFFIXES = Arrays.asList(
                " ",
                "K",
                "M",
                "B",
                "T",
                "Q",
                "QQ",
                "S",
                "SS",
                "O",
                "N",
                "D",
                "UN",
                "DD",
                "TR",
                "QT",
                "QN",
                "SD",
                "SPD",
                "OD",
                "ND",
                "VG",
                "UVG",
                "DVG",
                "TVG",
                "QTV",
                "QNV",
                "SEV",
                "SPV",
                "OVG",
                "NVG",
                "TG"
        );
    }

    public static String apply(double value) {
        if (isInvalid(value)) return "0";

        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            value = tmp;
            ++index;
        }

        return NUMBER_FORMAT.format(value) + SUFFIXES.get(index);
    }

    public static boolean isInvalid(double value) {
        return value < 0 || Double.isNaN(value) || Double.isInfinite(value);
    }

}