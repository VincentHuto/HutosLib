package com.vincenthuto.hutoslib.client.book;

import java.util.HashMap;
import java.util.Map;

public final class BookColors {
    private BookColors() {}

    static Map<String, Integer> derive(Map<String, Integer> explicit) {
        Map<String, Integer> colors = new HashMap<>(explicit);
        int paper = colors.get("paper"), ink = colors.get("ink"), accent = colors.get("accent");
        colors.putIfAbsent("paperEdge", lightness(paper, -0.12));
        colors.putIfAbsent("inkMuted", blend(ink, paper, 0.45));
        colors.putIfAbsent("inkRead", blend(ink, paper, 0.60));
        colors.putIfAbsent("accentHi", lightness(accent, 0.14));
        colors.putIfAbsent("accentLo", lightness(accent, -0.14));
        colors.putIfAbsent("link", accent);
        colors.putIfAbsent("linkLine", blend(colors.get("link"), paper, 0.60));
        colors.putIfAbsent("seal", accent);
        colors.putIfAbsent("notice", 0xFFF0D27A);
        colors.putIfAbsent("ribbon", colors.get("link"));
        colors.putIfAbsent("hand", accent);
        colors.putIfAbsent("wash", lightness(paper, 0.08));
        colors.putIfAbsent("frameInner", colors.get("accentLo"));
        colors.putIfAbsent("frameMid", accent);
        colors.putIfAbsent("frameOuter", 0xCC000000);
        colors.putIfAbsent("board", lightness(colors.get("frameMid"), -0.20));
        return Map.copyOf(colors);
    }

    static int parse(String value) {
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() == 3) hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        if (hex.length() != 6 && hex.length() != 8) throw new IllegalArgumentException("Expected #RGB, #RRGGBB or #AARRGGBB");
        return (int) Long.parseLong(hex, 16) | (hex.length() == 6 ? 0xFF000000 : 0);
    }

    static double luminance(int color) {
        return 0.2126 * linear((color >> 16) & 255) + 0.7152 * linear((color >> 8) & 255) + 0.0722 * linear(color & 255);
    }

    public static double contrast(int first, int second) {
        double a = luminance(first), b = luminance(second);
        return (Math.max(a, b) + 0.05) / (Math.min(a, b) + 0.05);
    }

    static int blend(int first, int second, double amount) {
        int color = 0;
        for (int shift : new int[]{0, 8, 16, 24}) {
            int a = first >>> shift & 255, b = second >>> shift & 255;
            color |= (int) Math.round(a * (1 - amount) + b * amount) << shift;
        }
        return color;
    }

    private static int lightness(int color, double delta) {
        double r = linear(color >> 16 & 255), g = linear(color >> 8 & 255), b = linear(color & 255);
        double l = Math.cbrt(0.4122214708*r + 0.5363325363*g + 0.0514459929*b);
        double m = Math.cbrt(0.2119034982*r + 0.6806995451*g + 0.1073969566*b);
        double s = Math.cbrt(0.0883024619*r + 0.2817188376*g + 0.6299787005*b);
        double light = Math.clamp(0.2104542553*l + 0.7936177850*m - 0.0040720468*s + delta, 0, 1);
        double a = 1.9779984951*l - 2.4285922050*m + 0.4505937099*s;
        double axisB = 0.0259040371*l + 0.7827717662*m - 0.8086757660*s;
        l = Math.pow(light + 0.3963377774*a + 0.2158037573*axisB, 3);
        m = Math.pow(light - 0.1055613458*a - 0.0638541728*axisB, 3);
        s = Math.pow(light - 0.0894841775*a - 1.2914855480*axisB, 3);
        return color & 0xFF000000
                | channel(4.0767416621*l - 3.3077115913*m + 0.2309699292*s) << 16
                | channel(-1.2684380046*l + 2.6097574011*m - 0.3413193965*s) << 8
                | channel(-0.0041960863*l - 0.7034186147*m + 1.7076147010*s);
    }

    private static double linear(int channel) {
        double value = channel / 255.0;
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static int channel(double linear) {
        double value = linear <= 0.0031308 ? 12.92 * linear : 1.055 * Math.pow(linear, 1.0 / 2.4) - 0.055;
        return (int) Math.round(Math.clamp(value, 0, 1) * 255);
    }
}
