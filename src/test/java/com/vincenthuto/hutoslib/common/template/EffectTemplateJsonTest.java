package com.vincenthuto.hutoslib.common.template;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

class EffectTemplateJsonTest {

	@Test
	void tendrilTemplateParsesWrappedConfigJson() {
		TendrilEffectConfig config = EffectTemplateJson.parseTendril("""
				{"type":"hutoslib:tendril","config":{"coreColor":-15658735,"glowColor":-2001269991,"fixedSeed":true,"seed":42,"segments":24}}
				""");

		assertEquals(0xFF111111, config.coreColor());
		assertEquals(0x88B70B19, config.glowColor());
		assertEquals(true, config.fixedSeed());
		assertEquals(42L, config.seed());
		assertEquals(24, config.segments());
	}

	@Test
	void lightningTemplateParsesRawConfigJson() {
		LightningTestConfig config = EffectTemplateJson.parseLightning("""
				{"backend":"PARTICLE","outerColor":-16711681,"innerColor":-1,"fixedSeed":true,"seed":99}
				""");

		assertEquals(LightningTestConfig.Backend.PARTICLE, config.backend());
		assertEquals(0xFF00FFFF, config.outerColor());
		assertEquals(0xFFFFFFFF, config.innerColor());
		assertEquals(true, config.fixedSeed());
		assertEquals(99L, config.seed());
	}

	@Test
	void templateJsonRoundTripsConfigValues() {
		String json = EffectTemplateJson.toTendrilJson(TendrilEffectConfig.defaults().withFixedSeed(true, 123L));

		TendrilEffectConfig config = EffectTemplateJson.parseTendril(json);

		assertEquals(true, config.fixedSeed());
		assertEquals(123L, config.seed());
		assertEquals(TendrilEffectConfig.defaults().coreColor(), config.coreColor());
	}
}
