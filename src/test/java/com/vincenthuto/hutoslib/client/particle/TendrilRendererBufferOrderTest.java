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
	void blendedModeWritesCoreBeforeGlow() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(4, 1, 0.16F, 0.2F)
				.withColors(0xFF12070A, 0xCCB40B18).withBlendColors(true);

		List<TendrilRenderer.GeometryPass> passes = passesFor(config);

		assertTrue(passes.contains(TendrilRenderer.GeometryPass.CORE));
		assertTrue(passes.contains(TendrilRenderer.GeometryPass.GLOW));
		int firstGlow = passes.indexOf(TendrilRenderer.GeometryPass.GLOW);
		assertTrue(passes.subList(firstGlow, passes.size()).stream()
				.noneMatch(pass -> pass == TendrilRenderer.GeometryPass.CORE));
	}

	@Test
	void separatedModeWritesGlowBeforeCoreSoInnerColorRemainsVisible() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(4, 1, 0.16F, 0.2F)
				.withColors(0xFF000000, 0xFFFFD65A).withBlendColors(false);

		List<TendrilRenderer.GeometryPass> passes = passesFor(config);

		assertTrue(passes.contains(TendrilRenderer.GeometryPass.GLOW));
		assertTrue(passes.contains(TendrilRenderer.GeometryPass.CORE));
		int firstCore = passes.indexOf(TendrilRenderer.GeometryPass.CORE);
		assertTrue(passes.subList(0, firstCore).stream().anyMatch(pass -> pass == TendrilRenderer.GeometryPass.GLOW));
	}

	private static List<TendrilRenderer.GeometryPass> passesFor(TendrilEffectConfig config) {
		TendrilGeometry geometry = TendrilGeometry.generate(Vec3.ZERO, new Vec3(1.5, 0.4, 0.0), config, 42L,
				0.0F);
		List<TendrilRenderer.GeometryPass> passes = new ArrayList<>();

		assertDoesNotThrow(() -> TendrilRenderer.emitGeometryPasses(geometry, config, 1.0F, 1.0F,
				(pass, vertices) -> {
					if (!vertices.isEmpty()) {
						passes.add(pass);
					}
				}));
		return passes;
	}
}
