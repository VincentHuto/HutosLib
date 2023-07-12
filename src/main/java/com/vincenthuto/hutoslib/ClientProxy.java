package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.client.HlClientTickHandler;
import com.vincenthuto.hutoslib.client.particle.BoltRenderer;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData;
import com.vincenthuto.hutoslib.client.particle.data.BoltParticleData.FadeFunction;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.world.phys.Vec3;

public class ClientProxy implements IProxy {

	@Override
	public void lightningFX(Vec3 vectorStart, Vec3 vectorEnd, float ticksPerMeter, long seed, int colorOuter,
			int colorInner) {
		BoltRenderer.INSTANCE.add(new BoltParticleData(vectorStart, vectorEnd).size(0.08F),
				HlClientTickHandler.partialTicks);

	}

	@Override
	public void lightningFX(Vec3 vectorStart, Vec3 vectorEnd, float ticksPerMeter, ParticleColor color) {

		BoltRenderer.INSTANCE.add(
				new BoltParticleData(vectorStart, vectorEnd, color).size(0.08F).fade(FadeFunction.fade(0.125f)),
				HlClientTickHandler.partialTicks);
	}

}
