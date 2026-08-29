package com.vincenthuto.hutoslib.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import net.minecraft.resources.ResourceLocation;

class HLClientEventsTest {

	@Test
	void boltRendererOnlyRunsAfterParticles() {
		assertTrue(HLClientEvents.shouldRenderBoltStage("after_particles"));
		assertTrue(HLClientEvents.shouldRenderBoltStage("minecraft:after_particles"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_sky"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_entities"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_level"));
	}

	@Test
	void wideEffectHoverUsesVanillaCompressedRowSpacing() {
		assertEquals(2, HLClientEvents.hoveredEffectIndex(6, 20, 73));
		assertEquals(1, HLClientEvents.hoveredEffectIndex(5, 20, 53));
		assertEquals(-1, HLClientEvents.hoveredEffectIndex(6, 20, 19));
	}

	@Test
	void effectTooltipLineAcceptsRegistryIds() throws ReflectiveOperationException {
		Method line = HLClientEvents.class.getDeclaredMethod("line", String.class, Object[].class);
		line.setAccessible(true);

		assertDoesNotThrow(() -> line.invoke(null, "effect_id",
				new Object[] { ResourceLocation.parse("minecraft:regeneration") }));
	}
}
