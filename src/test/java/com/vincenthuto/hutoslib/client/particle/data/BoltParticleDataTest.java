package com.vincenthuto.hutoslib.client.particle.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.Vec3;

class BoltParticleDataTest {

	@Test
	@SuppressWarnings("unchecked")
	void createQuadsKeepsWidthForDiagonalSegments() throws ReflectiveOperationException {
		BoltParticleData data = new BoltParticleData(Vec3.ZERO, new Vec3(1, 1, 1));
		Method method = BoltParticleData.class.getDeclaredMethod("createQuads", Class.forName(
				"com.vincenthuto.hutoslib.client.particle.data.BoltParticleData$QuadCache"), Vec3.class, Vec3.class,
				float.class);
		method.setAccessible(true);

		Pair<BoltParticleData.BoltQuads, ?> result = (Pair<BoltParticleData.BoltQuads, ?>) method.invoke(data, null,
				Vec3.ZERO, new Vec3(1, 1, 1), 0.08F);

		Set<Vec3> uniqueVertices = new HashSet<>(result.getLeft().getVecs());

		assertTrue(uniqueVertices.size() > 2, "diagonal segment should produce real width, not a collapsed line");
	}

	@Test
	@SuppressWarnings("unchecked")
	void createQuadsCentersRibbonOnBoltPath() throws ReflectiveOperationException {
		BoltParticleData data = new BoltParticleData(Vec3.ZERO, new Vec3(1, 0, 0));
		Method method = BoltParticleData.class.getDeclaredMethod("createQuads", Class.forName(
				"com.vincenthuto.hutoslib.client.particle.data.BoltParticleData$QuadCache"), Vec3.class, Vec3.class,
				float.class);
		method.setAccessible(true);

		Pair<BoltParticleData.BoltQuads, ?> result = (Pair<BoltParticleData.BoltQuads, ?>) method.invoke(data, null,
				Vec3.ZERO, new Vec3(1, 0, 0), 0.2F);

		List<Vec3> firstQuad = result.getLeft().getVecs().subList(0, 4);
		Vec3 average = firstQuad.stream().reduce(Vec3.ZERO, Vec3::add).scale(0.25);

		assertTrue(average.distanceTo(new Vec3(0.5, 0, 0)) < 1.0E-6,
				"bolt ribbon should be centered on the generated segment, not offset to one side");
	}
}
