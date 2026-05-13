package com.vincenthuto.hutoslib.common.util;

public final class HLTextUtils {
	private HLTextUtils() {
	}

	public static String convertInitToLang(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder converted = new StringBuilder();
		boolean convertNext = true;
		for (char ch : text.replace("_trail", "").toCharArray()) {
			if (ch == '_') {
				ch = ' ';
				convertNext = true;
			} else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			} else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}
		return converted.toString();
	}
}
