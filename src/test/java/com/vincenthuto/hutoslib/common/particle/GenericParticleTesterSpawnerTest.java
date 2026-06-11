package com.vincenthuto.hutoslib.common.particle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.Vec3;

class GenericParticleTesterSpawnerTest {

	@Test
	void shapedDefaultsUseReadableRadius() {
		GenericParticleTestConfig config = GenericParticleTestConfig.defaults()
				.withParticleShape(GenericParticleTestConfig.Shape.FIBONACCI_SPHERE);

		double maxDistance = GenericParticleTesterSpawner.shapePoints(config, 0).stream()
				.mapToDouble(Vec3::length)
				.max()
				.orElse(0.0D);

		assertTrue(maxDistance >= 1.0D, "default shaped particles should spread over a visible radius");
	}

	@Test
	void allShapePointsAreFiniteAcrossRotations() {
		for (GenericParticleTestConfig.Shape shape : GenericParticleTestConfig.Shape.values()) {
			if (shape == GenericParticleTestConfig.Shape.BURST) {
				continue;
			}
			for (long gameTime : new long[] { 0L, 37L, 96L, 157L, 239L }) {
				GenericParticleTestConfig config = GenericParticleTestConfig.defaults().withParticleShape(shape);

				for (Vec3 point : GenericParticleTesterSpawner.shapePoints(config, gameTime)) {
					assertTrue(Double.isFinite(point.x), shape + " produced non-finite x at " + gameTime);
					assertTrue(Double.isFinite(point.y), shape + " produced non-finite y at " + gameTime);
					assertTrue(Double.isFinite(point.z), shape + " produced non-finite z at " + gameTime);
				}
			}
		}
	}
}
