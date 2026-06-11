package com.vincenthuto.hutoslib.common.tendril;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

class TendrilTesterBlockTargetTest {

	@Test
	void verticalDefaultOffsetExpandsIntoRadialHorizontalTarget() {
		Vec3 start = new Vec3(4.5D, 65.5D, -2.5D);

		Vec3 end = TendrilTesterBlockTarget.end(start, new BlockPos(4, 65, -3), TendrilEffectConfig.defaults(), 0L);

		assertEquals(start.y + TendrilEffectConfig.defaults().targetOffsetY(), end.y);
		assertTrue(new Vec3(end.x - start.x, 0.0D, end.z - start.z).length() > 2.0D);
	}

	@Test
	void radialFallbackRotatesOverRepeatedSpawns() {
		Vec3 start = Vec3.ZERO;
		BlockPos pos = new BlockPos(0, 64, 0);

		Vec3 first = TendrilTesterBlockTarget.end(start, pos, TendrilEffectConfig.defaults(), 0L);
		Vec3 second = TendrilTesterBlockTarget.end(start, pos, TendrilEffectConfig.defaults(), 20L);

		assertNotEquals(first, second);
	}

	@Test
	void radialFallbackRotatesOverManualClicksWithoutWaitingForRepeatInterval() {
		Vec3 start = Vec3.ZERO;
		BlockPos pos = new BlockPos(0, 64, 0);

		Vec3 first = TendrilTesterBlockTarget.endForManualSpawn(start, pos, TendrilEffectConfig.defaults(), 0);
		Vec3 second = TendrilTesterBlockTarget.endForManualSpawn(start, pos, TendrilEffectConfig.defaults(), 1);

		assertNotEquals(first, second);
	}

	@Test
	void customHorizontalOffsetIsPreservedExactly() {
		Vec3 start = new Vec3(1.0D, 2.0D, 3.0D);
		TendrilEffectConfig config = TendrilEffectConfig.defaults().withTargetOffset(3.0F, 2.0F, -4.0F);

		Vec3 end = TendrilTesterBlockTarget.end(start, BlockPos.ZERO, config, 200L);

		assertEquals(start.add(3.0D, 2.0D, -4.0D), end);
	}
}
