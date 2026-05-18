package com.vincenthuto.hutoslib.client.render;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.client.render.item.ArmBannerRenderState;

class ArmBannerRenderStateTest {
	@Test
	void rendersPlateForBannerWithNoPatterns() {
		ArmBannerRenderState state = ArmBannerRenderState.fromBannerData(true, 0);

		assertTrue(state.shouldRenderPlate());
		assertFalse(state.hasPatternLayers());
	}

	@Test
	void rendersPlateForBannerWithPatternsEvenWithoutBaseColor() {
		ArmBannerRenderState state = ArmBannerRenderState.fromBannerData(false, 1);

		assertTrue(state.shouldRenderPlate());
		assertTrue(state.hasPatternLayers());
	}

	@Test
	void doesNotRenderPlateWhenNoBannerDataWasCopied() {
		ArmBannerRenderState state = ArmBannerRenderState.fromBannerData(false, 0);

		assertFalse(state.shouldRenderPlate());
		assertFalse(state.hasPatternLayers());
	}
}
