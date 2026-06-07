package com.vincenthuto.hutoslib.client.particle.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
