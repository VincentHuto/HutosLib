package com.vincenthuto.hutoslib.common.tendril;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

class TendrilEffectConfigTest {

	@Test
	void defaultsAreDevelopmentSafe() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults();

		assertEquals(TendrilEffectConfig.Mode.FREEFORM, config.mode());
		assertEquals(16.0F, config.range());
		assertEquals(10, config.growTicks());
		assertEquals(10, config.holdTicks());
		assertEquals(10, config.fadeTicks());
		assertEquals(16, config.segments());
		assertEquals(1, config.strandCount());
		assertEquals(0.12F, config.baseWidth());
		assertEquals(0.12F, config.surfaceLift());
	}

	@Test
	void clampsExtremeValues() {
		TendrilEffectConfig config = new TendrilEffectConfig(null, 0xFFFFFFFF, 0x80FF0000, -2.0F, 999.0F,
				-999.0F, 999.0F, 0, 0, 0, 1, 0, -1.0F, -2.0F, -4, -1, -5.0F, -2.0F, -1.0F, -1.0F, -9.0F,
				9.0F, -3.0F, -1.0F, true, 42L, true, 1);

		TendrilEffectConfig clamped = config.clamped();

		assertEquals(TendrilEffectConfig.Mode.FREEFORM, clamped.mode());
		assertEquals(1.0F, clamped.range());
		assertEquals(64.0F, clamped.targetOffsetX());
		assertEquals(-64.0F, clamped.targetOffsetY());
		assertEquals(1, clamped.growTicks());
		assertEquals(1, clamped.holdTicks());
		assertEquals(1, clamped.fadeTicks());
		assertEquals(2, clamped.segments());
		assertEquals(1, clamped.strandCount());
		assertEquals(0.01F, clamped.baseWidth());
		assertEquals(0.01F, clamped.tipScale());
		assertEquals(0, clamped.branchCount());
		assertEquals(0, clamped.branchDepth());
		assertEquals(0.05F, clamped.branchLength());
		assertEquals(0.0F, clamped.branchSpread());
		assertEquals(0.0F, clamped.writheAmplitude());
		assertEquals(0.001F, clamped.writheFrequency());
		assertEquals(-4.0F, clamped.curl());
		assertEquals(4.0F, clamped.sag());
		assertEquals(0.0F, clamped.surfaceSnapDistance());
		assertEquals(0.0F, clamped.surfaceLift());
		assertEquals(5, clamped.repeatInterval());
	}

	@Test
	void nbtRoundTripPreservesConfig() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withMode(TendrilEffectConfig.Mode.SURFACE)
				.withTargetOffset(2.0F, 3.0F, 4.0F).withLifecycle(12, 20, 8).withShape(24, 2, 0.18F, 0.08F)
				.withBranching(5, 2, 0.42F, 0.9F).withWrithe(0.2F, 0.07F, 1.4F, -0.3F)
				.withSurface(2.5F, 0.18F).withFixedSeed(true, 1234L).withRepeat(true, 16);

		assertEquals(config, TendrilEffectConfig.fromTag(config.toTag()));
	}

	@Test
	void bufferRoundTripPreservesConfig() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withMode(TendrilEffectConfig.Mode.SURFACE)
				.withColors(0xCC11070A, 0x88B70B19).withTargetOffset(-2.0F, 4.0F, 1.0F)
				.withLifecycle(6, 14, 9).withShape(28, 3, 0.22F, 0.05F).withBranching(6, 3, 0.3F, 1.2F)
				.withWrithe(0.28F, 0.09F, 0.8F, 0.4F).withSurface(3.0F, 0.2F)
				.withFixedSeed(true, 9876L).withRepeat(true, 25);
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		config.toBuffer(buf);

		assertEquals(config, TendrilEffectConfig.fromBuffer(buf));
	}
}
