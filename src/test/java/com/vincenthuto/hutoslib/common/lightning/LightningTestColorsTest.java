package com.vincenthuto.hutoslib.common.lightning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LightningTestColorsTest {

	@Test
	void parsesCommonColorNames() {
		assertEquals(LightningTestColors.parseColor("#FFFFFF").orElseThrow(),
				LightningTestColors.parseColor("white").orElseThrow());
		assertEquals(0xFF00FFFF, LightningTestColors.parseColor("cyan").orElseThrow());
		assertEquals(0xFFFF00FF, LightningTestColors.parseColor("MAGENTA").orElseThrow());
		assertEquals(0xFFFFA500, LightningTestColors.parseColor(" orange ").orElseThrow());
	}

	@Test
	void parsesRgbAndArgbHexValues() {
		assertEquals(0xFF12ABEF, LightningTestColors.parseColor("#12abef").orElseThrow());
		assertEquals(0x8012ABEF, LightningTestColors.parseColor("0x8012abef").orElseThrow());
		assertEquals(0xFF00AA11, LightningTestColors.parseColor("00AA11").orElseThrow());
	}

	@Test
	void parsesCssRgbaHexValues() {
		assertEquals(0x8012ABEF, LightningTestColors.parseColor("#12abef80").orElseThrow());
		assertEquals(0x4000FF00, LightningTestColors.parseColor("00ff0040").orElseThrow());
	}

	@Test
	void rejectsInvalidColorInput() {
		assertTrue(LightningTestColors.parseColor("").isEmpty());
		assertTrue(LightningTestColors.parseColor("storm-blue").isEmpty());
		assertTrue(LightningTestColors.parseColor("#12345").isEmpty());
		assertTrue(LightningTestColors.parseColor("#zzzzzz").isEmpty());
	}

	@Test
	void formatsKnownNamesBeforeHexValues() {
		assertEquals("cyan", LightningTestColors.displayName(0xFF00FFFF));
		assertEquals("#123456", LightningTestColors.displayName(0xFF123456));
		assertEquals("#80123456", LightningTestColors.displayName(0x80123456));
	}

	@Test
	void cyclesPaletteFromCustomColorIntoFirstPreset() {
		assertEquals(0xFFFFFFFF, LightningTestColors.nextPreset(0xFF123456));
		assertEquals(0xFF000000, LightningTestColors.previousPreset(0xFF123456));
	}
}
