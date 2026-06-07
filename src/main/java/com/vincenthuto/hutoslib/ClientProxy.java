package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.client.HlClientTickHandler;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData.FadeFunction;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.world.phys.Vec3;

public class ClientProxy implements IProxy {

	private static int lifespan(Vec3 vectorStart, Vec3 vectorEnd, float ticksPerMeter) {
		if (ticksPerMeter <= 0) {
			return 30;
		}
		return Math.max(1, Math.min(30, (int) Math.ceil(vectorStart.distanceTo(vectorEnd) * ticksPerMeter)));
	}

	@Override
	public void lightningFX(Vec3 vectorStart, Vec3 vectorEnd, float ticksPerMeter, long seed, int colorOuter,
			int colorInner) {
		int lifespan = lifespan(vectorStart, vectorEnd, ticksPerMeter);
		float partialTicks = HlClientTickHandler.partialTicks;
		BoltRenderer.INSTANCE.add(new BoltParticleData(vectorStart, vectorEnd, seed, colorOuter).size(0.08F)
				.lifespan(lifespan).fade(FadeFunction.fade(0.125f)), partialTicks);
		BoltRenderer.INSTANCE.add(new BoltParticleData(vectorStart, vectorEnd, seed ^ 0x9E3779B97F4A7C15L, colorInner)
				.size(0.035F).lifespan(lifespan).fade(FadeFunction.fade(0.125f)), partialTicks);

	}

	@Override
	public void lightningFX(Vec3 vectorStart, Vec3 vectorEnd, float ticksPerMeter, ParticleColor color) {

		BoltRenderer.INSTANCE.add(
				new BoltParticleData(vectorStart, vectorEnd, color).size(0.08F).lifespan(lifespan(vectorStart, vectorEnd, ticksPerMeter))
						.fade(FadeFunction.fade(0.125f)),
				HlClientTickHandler.partialTicks);
	}

}
