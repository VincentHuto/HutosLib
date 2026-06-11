package com.vincenthuto.hutoslib.common.particle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

class GenericParticleTestConfigTest {

	@Test
	void defaultsAreUsefulForTester() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults();

		assertEquals(GenericParticleTestConfig.Kind.GLOW, config.kind());
		assertEquals(GenericParticleTestConfig.Shape.BURST, config.shape());
		assertEquals(0xFFFF19B4, config.color());
		assertEquals(false, config.randomColor());
		assertEquals(16, config.count());
		assertEquals(0.35F, config.spread());
		assertEquals(0.02F, config.speed());
		assertEquals(24, config.life());
		assertEquals(0.35F, config.scale());
		assertEquals(0.8F, config.alpha());
	}

	@Test
	void cyclesParticleTypes() {
		assertEquals(GenericParticleTestConfig.Kind.EMBER, GenericParticleTestConfig.Kind.GLOW.next());
		assertEquals(GenericParticleTestConfig.Kind.DARK_GLOW, GenericParticleTestConfig.Kind.EMBER.next());
		assertEquals(GenericParticleTestConfig.Kind.GLOW, GenericParticleTestConfig.Kind.DARK_GLOW.next());
		assertEquals(GenericParticleTestConfig.Kind.DARK_GLOW, GenericParticleTestConfig.Kind.GLOW.previous());
	}

	@Test
	void cyclesParticleShapes() {
		assertEquals(GenericParticleTestConfig.Shape.FIBONACCI_SPHERE, GenericParticleTestConfig.Shape.BURST.next());
		assertEquals(GenericParticleTestConfig.Shape.TANGENT_FUNNEL, GenericParticleTestConfig.Shape.BURST.previous());
		assertEquals(GenericParticleTestConfig.Shape.LOTUS_FOUNTAIN,
				GenericParticleTestConfig.Shape.BLOOMING_FLOWER.previous());
	}

	@Test
	void clampsExtremeValues() {
		GenericParticleTestConfig config = new GenericParticleTestConfig(null, null, 0xFFFFFFFF, false, 0, -4.0F, -3.0F,
				-2.0F, -1.0F, 0, 999, true, 0);

		GenericParticleTestConfig clamped = config.clamped();

		assertEquals(GenericParticleTestConfig.Kind.GLOW, clamped.kind());
		assertEquals(GenericParticleTestConfig.Shape.BURST, clamped.shape());
		assertEquals(1, clamped.count());
		assertEquals(0.0F, clamped.spread());
		assertEquals(0.0F, clamped.speed());
		assertEquals(0.05F, clamped.scale());
		assertEquals(0.0F, clamped.alpha());
		assertEquals(1, clamped.life());
		assertEquals(128.0F, clamped.range());
		assertEquals(1, clamped.repeatInterval());
	}

	@Test
	void repeatIntervalAllowsSingleTick() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults().withRepeat(true, 1);

		assertEquals(1, config.repeatInterval());
		assertEquals(config, GenericParticleTestConfig.fromTag(config.toTag()));
	}

	@Test
	void randomColorRoundTripsThroughNbtAndBuffer() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults().withRandomColor(true);
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		config.toBuffer(buf);

		assertEquals(true, GenericParticleTestConfig.fromTag(config.toTag()).randomColor());
		assertEquals(config, GenericParticleTestConfig.fromBuffer(buf));
	}

	@Test
	void nbtRoundTripPreservesConfig() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults().withKind(GenericParticleTestConfig.Kind.EMBER)
				.withParticleShape(GenericParticleTestConfig.Shape.COSMIC_BIRTH).withColor(0xFF42FFF0)
				.withShape(32, 0.75F, 0.12F).withEmber(0.65F, 0.9F, 44)
				.withRange(18.0F).withRepeat(true, 25);

		assertEquals(config, GenericParticleTestConfig.fromTag(config.toTag()));
	}

	@Test
	void bufferRoundTripPreservesConfig() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults()
				.withKind(GenericParticleTestConfig.Kind.DARK_GLOW).withColor(0xFF150020)
				.withParticleShape(GenericParticleTestConfig.Shape.IMPLODE).withShape(24, 0.4F, 0.04F)
				.withEmber(0.9F, 0.5F, 36).withRange(12.0F).withRepeat(true, 40);
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		config.toBuffer(buf);

		assertEquals(config, GenericParticleTestConfig.fromBuffer(buf));
	}
}
