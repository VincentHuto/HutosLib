package com.vincenthuto.hutoslib.client.particle.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.world.phys.Vec3;

class TendrilGeometryTest {

	@Test
	void generatedGeometryIsDeterministicForSeed() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(12, 2, 0.16F, 0.1F)
				.withBranching(3, 2, 0.35F, 0.8F);

		TendrilGeometry first = TendrilGeometry.generate(Vec3.ZERO, new Vec3(3.0, 1.0, 0.5), config, 123L, 0.0F);
		TendrilGeometry second = TendrilGeometry.generate(Vec3.ZERO, new Vec3(3.0, 1.0, 0.5), config, 123L, 0.0F);

		assertEquals(first, second);
	}

	@Test
	void mainStrandTapersFromBaseToTip() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(8, 1, 0.2F, 0.25F);

		TendrilGeometry geometry = TendrilGeometry.generate(Vec3.ZERO, new Vec3(2.0, 0.0, 0.0), config, 1L, 0.0F);
		TendrilGeometry.Strand main = geometry.strands().get(0);

		assertEquals(0.2F, main.rings().get(0).width(), 1.0E-6F);
		assertEquals(0.05F, main.rings().get(main.rings().size() - 1).width(), 1.0E-6F);
	}

	@Test
	void tubeQuadsDoNotCollapseOnDiagonalSegments() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(4, 1, 0.2F, 0.1F);
		TendrilGeometry geometry = TendrilGeometry.generate(Vec3.ZERO, new Vec3(1.0, 1.0, 1.0), config, 2L, 0.0F);

		List<Vec3> vertices = TendrilGeometry.createTubeQuads(geometry.strands().get(0), 1.0F).vertices();
		Set<Vec3> uniqueVertices = new HashSet<>(vertices);

		assertTrue(uniqueVertices.size() > 4, "diagonal tube should have real volume, not collapse to a line");
	}

	@Test
	void branchGenerationRespectsGlobalCountAndDepthLimits() {
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withShape(12, 1, 0.16F, 0.08F)
				.withBranching(4, 2, 0.4F, 1.0F);

		TendrilGeometry geometry = TendrilGeometry.generate(Vec3.ZERO, new Vec3(4.0, 0.5, 0.0), config, 99L, 0.0F);

		long branchCount = geometry.strands().stream().filter(TendrilGeometry.Strand::branch).count();
		int maxDepth = geometry.strands().stream().mapToInt(TendrilGeometry.Strand::depth).max().orElse(0);

		assertTrue(branchCount <= 4, "branch budget should cap generated branches");
		assertTrue(maxDepth <= 2, "branch depth should not exceed config");
	}
}
