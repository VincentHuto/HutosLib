package com.vincenthuto.hutoslib.common.lightning;

import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData.FadeFunction;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData.BoltRenderInfo;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData.SpawnFunction;

import net.minecraft.world.phys.Vec3;

public final class LightningTestBoltFactory {
	private LightningTestBoltFactory() {
	}

	public static BoltParticleData create(Vec3 start, Vec3 end, long seed, int color, float size,
			LightningTestConfig config) {
		LightningTestConfig clamped = config.clamped();
		BoltRenderInfo info = new BoltParticleData(start, end, seed, color).getRenderInfo()
				.noise(0.1F, clamped.maxOffset());
		return new BoltParticleData(info, start, end, clamped.fract(), 1, size, lifespan(start, end, clamped),
				SpawnFunction.NO_DELAY, FadeFunction.fade(0.125F), seed);
	}

	private static int lifespan(Vec3 start, Vec3 end, LightningTestConfig config) {
		double rawTicks = start.distanceTo(end) * config.ticksPerMeter() / config.speed();
		return Math.max(1, Math.min(config.maxAge(), (int) Math.ceil(rawTicks)));
	}
}
