package com.vincenthuto.hutoslib.common.tendril;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class TendrilTesterBlockTarget {
	private static final double GOLDEN_ANGLE = Math.PI * (3.0D - Math.sqrt(5.0D));
	private static final double MIN_RADIAL_RADIUS = 2.5D;
	private static final double RADIAL_RANGE_SCALE = 0.35D;
	private static final double MAX_RADIAL_RADIUS = 8.0D;

	private TendrilTesterBlockTarget() {
	}

	public static Vec3 end(Vec3 start, BlockPos pos, TendrilEffectConfig config, long gameTime) {
		return endAtStep(start, pos, config, spawnStep(gameTime, config));
	}

	public static Vec3 endForManualSpawn(Vec3 start, BlockPos pos, TendrilEffectConfig config, long manualStep) {
		return endAtStep(start, pos, config, manualStep);
	}

	private static Vec3 endAtStep(Vec3 start, BlockPos pos, TendrilEffectConfig config, long step) {
		TendrilEffectConfig clamped = config.clamped();
		Vec3 offset = clamped.targetOffset();
		if (offset.x * offset.x + offset.z * offset.z > 1.0E-6D) {
			return start.add(offset);
		}
		double radius = Math.max(MIN_RADIAL_RADIUS, Math.min(MAX_RADIAL_RADIUS, clamped.range() * RADIAL_RANGE_SCALE));
		double angle = baseAngle(pos) + step * GOLDEN_ANGLE;
		return start.add(Math.cos(angle) * radius, offset.y, Math.sin(angle) * radius);
	}

	private static double baseAngle(BlockPos pos) {
		long value = pos.asLong();
		value ^= value >>> 33;
		value *= 0xff51afd7ed558ccdL;
		value ^= value >>> 33;
		value *= 0xc4ceb9fe1a85ec53L;
		value ^= value >>> 33;
		return ((value & 0xFFFFL) / 65536.0D) * Math.PI * 2.0D;
	}

	private static long spawnStep(long gameTime, TendrilEffectConfig config) {
		return gameTime / Math.max(1, config.repeatInterval());
	}
}
