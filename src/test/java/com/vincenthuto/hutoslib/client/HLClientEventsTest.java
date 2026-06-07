package com.vincenthuto.hutoslib.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HLClientEventsTest {

	@Test
	void boltRendererOnlyRunsAfterParticles() {
		assertTrue(HLClientEvents.shouldRenderBoltStage("after_particles"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_sky"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_entities"));
		assertFalse(HLClientEvents.shouldRenderBoltStage("after_level"));
	}
}
