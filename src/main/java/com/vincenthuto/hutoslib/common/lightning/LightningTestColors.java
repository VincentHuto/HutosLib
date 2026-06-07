package com.vincenthuto.hutoslib.common.lightning;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;

public final class LightningTestColors {
	private static final Map<String, Integer> NAMED_COLORS = new LinkedHashMap<>();
	private static final int[] PRESETS = {
			0xFFFFFFFF, 0xFF00FFFF, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFA500,
			0xFF8000FF, 0xFF000000
	};

	static {
		NAMED_COLORS.put("white", 0xFFFFFFFF);
		NAMED_COLORS.put("cyan", 0xFF00FFFF);
		NAMED_COLORS.put("red", 0xFFFF0000);
		NAMED_COLORS.put("green", 0xFF00FF00);
		NAMED_COLORS.put("blue", 0xFF0000FF);
		NAMED_COLORS.put("magenta", 0xFFFF00FF);
		NAMED_COLORS.put("yellow", 0xFFFFFF00);
		NAMED_COLORS.put("orange", 0xFFFFA500);
		NAMED_COLORS.put("purple", 0xFF8000FF);
		NAMED_COLORS.put("black", 0xFF000000);
	}

	private LightningTestColors() {
	}

	public static String displayName(int color) {
		for (Map.Entry<String, Integer> entry : NAMED_COLORS.entrySet()) {
			if (entry.getValue() == color) {
				return entry.getKey();
			}
		}
		return (color >>> 24) == 0xFF ? String.format("#%06X", color & 0xFFFFFF) : String.format("#%08X", color);
	}

	public static int nextPreset(int color) {
		for (int i = 0; i < PRESETS.length; i++) {
			if (PRESETS[i] == color) {
				return PRESETS[(i + 1) % PRESETS.length];
			}
		}
		return PRESETS[0];
	}

	public static OptionalInt parseColor(String input) {
		if (input == null) {
			return OptionalInt.empty();
		}
		String value = input.trim().toLowerCase(Locale.ROOT);
		if (value.isEmpty()) {
			return OptionalInt.empty();
		}
		Integer named = NAMED_COLORS.get(value);
		if (named != null) {
			return OptionalInt.of(named);
		}
		if (value.startsWith("#")) {
			value = value.substring(1);
		} else if (value.startsWith("0x")) {
			value = value.substring(2);
		}
		if (value.length() != 6 && value.length() != 8) {
			return OptionalInt.empty();
		}
		try {
			long parsed = Long.parseUnsignedLong(value, 16);
			if (value.length() == 6) {
				parsed |= 0xFF000000L;
			}
			return OptionalInt.of((int) parsed);
		} catch (NumberFormatException ignored) {
			return OptionalInt.empty();
		}
	}

	public static int previousPreset(int color) {
		for (int i = 0; i < PRESETS.length; i++) {
			if (PRESETS[i] == color) {
				return PRESETS[(i + PRESETS.length - 1) % PRESETS.length];
			}
		}
		return PRESETS[PRESETS.length - 1];
	}
}
