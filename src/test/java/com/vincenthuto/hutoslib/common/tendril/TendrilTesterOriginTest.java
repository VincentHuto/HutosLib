package com.vincenthuto.hutoslib.common.tendril;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.world.phys.Vec3;

class TendrilTesterOriginTest {

	@Test
	void shiftedEyePositionStartsAheadBelowAndToTheSideOfTheCamera() {
		Vec3 eye = new Vec3(0.0D, 1.62D, 0.0D);

		Vec3 origin = TendrilTesterOrigin.shiftedEyePosition(eye, new Vec3(0.0D, 0.0D, 1.0D), 1);

		assertTrue(origin.z > eye.z + 0.75D);
		assertTrue(origin.y < eye.y - 0.45D);
		assertTrue(Math.abs(origin.x - eye.x) > 0.35D);
	}

	@Test
	void shiftedEyePositionKeepsSideOffsetEvenWhenLookingStraightUp() {
		Vec3 eye = new Vec3(2.0D, 3.0D, 4.0D);

		Vec3 origin = TendrilTesterOrigin.shiftedEyePosition(eye, new Vec3(0.0D, 1.0D, 0.0D), -1);

		assertTrue(origin.distanceToSqr(eye) > 0.5D);
		assertTrue(origin.x < eye.x - 0.35D);
	}

	@Test
	void shiftedEyePositionFallsBackWhenLookVectorIsZero() {
		Vec3 eye = new Vec3(0.0D, 1.0D, 0.0D);

		Vec3 origin = TendrilTesterOrigin.shiftedEyePosition(eye, Vec3.ZERO, 1);

		assertTrue(origin.z > eye.z + 0.75D);
		assertTrue(origin.y < eye.y);
	}
}
