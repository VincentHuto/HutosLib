package com.vincenthuto.hutoslib.common.lightning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LightningTestConfigTest {

	@Test
	void defaultsAreDevelopmentSafe() {
		LightningTestConfig config = LightningTestConfig.defaults();

		assertEquals(LightningTestConfig.Backend.BOLT, config.backend());
		assertEquals(16.0F, config.range());
		assertEquals(64.0F, config.ticksPerMeter());
		assertEquals(9, config.fract());
	}

	@Test
	void clampsExtremeValues() {
		LightningTestConfig config = new LightningTestConfig(LightningTestConfig.Backend.PARTICLE, 0xFFFFFF, 0xFFFFFF,
				0xFFFFFF, -20.0F, 999.0F, 999.0F, 999.0F, -2.0F, 500.0F, 0, 99, -4.0F, -1.0F, true, 0L, true, 1);

		LightningTestConfig clamped = config.clamped();

		assertEquals(1.0F, clamped.range());
		assertEquals(64.0F, clamped.targetOffsetX());
		assertEquals(1.0F, clamped.ticksPerMeter());
		assertEquals(20.0F, clamped.speed());
		assertEquals(1, clamped.maxAge());
		assertEquals(12, clamped.fract());
		assertEquals(0.01F, clamped.maxOffset());
		assertEquals(0.01F, clamped.size());
		assertEquals(5, clamped.repeatInterval());
	}

	@Test
	void nbtRoundTripPreservesConfig() {
		LightningTestConfig config = LightningTestConfig.defaults().withBackend(LightningTestConfig.Backend.PARTICLE)
				.withColorPreset(0xFF00FFFF).withTargetOffset(2.0F, 3.0F, 4.0F).withFixedSeed(true, 42L)
				.withRepeat(true, 12);

		assertEquals(config, LightningTestConfig.fromTag(config.toTag()));
	}
}
