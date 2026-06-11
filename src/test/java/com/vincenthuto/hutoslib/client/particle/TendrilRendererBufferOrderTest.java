package com.vincenthuto.hutoslib.client.particle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.client.particle.data.TendrilGeometry;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.world.phys.Vec3;

class TendrilRendererBufferOrderTest {

	@Test
	void renderGeometryDoesNotWriteToCoreAfterRequestingGlowBuffer() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(4, 1, 0.16F, 0.2F)
				.withColors(0xFF12070A, 0xCCB40B18);
		TendrilGeometry geometry = TendrilGeometry.generate(Vec3.ZERO, new Vec3(1.5, 0.4, 0.0), config, 42L,
				0.0F);
		List<TendrilRenderer.GeometryPass> passes = new ArrayList<>();

		assertDoesNotThrow(() -> TendrilRenderer.emitGeometryPasses(geometry, config, 1.0F, 1.0F,
				(pass, vertices) -> {
					if (!vertices.isEmpty()) {
						passes.add(pass);
					}
				}));
		assertTrue(passes.contains(TendrilRenderer.GeometryPass.CORE));
		assertTrue(passes.contains(TendrilRenderer.GeometryPass.GLOW));
		int firstGlow = passes.indexOf(TendrilRenderer.GeometryPass.GLOW);
		assertTrue(passes.subList(firstGlow, passes.size()).stream()
				.noneMatch(pass -> pass == TendrilRenderer.GeometryPass.CORE));
	}
}
