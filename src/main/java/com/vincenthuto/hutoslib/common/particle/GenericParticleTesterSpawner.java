package com.vincenthuto.hutoslib.common.particle;

import java.util.ArrayList;
import java.util.List;

import com.vincenthuto.hutoslib.client.particle.data.ColorParticleData;
import com.vincenthuto.hutoslib.client.particle.data.DarkColorParticleData;
import com.vincenthuto.hutoslib.client.particle.data.EmberParticleData;
import com.vincenthuto.hutoslib.client.particle.util.HLParticleUtils;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public final class GenericParticleTesterSpawner {
	private GenericParticleTesterSpawner() {
	}

	public static void spawn(ServerLevel level, Vec3 pos, GenericParticleTestConfig config) {
		GenericParticleTestConfig clamped = config.clamped();
		ParticleOptions options = options(level, clamped);
		if (clamped.shape() == GenericParticleTestConfig.Shape.BURST) {
			level.sendParticles(options, pos.x, pos.y, pos.z, clamped.count(), clamped.spread(), clamped.spread(),
					clamped.spread(), clamped.speed());
			return;
		}

		List<Vec3> points = shapePoints(clamped, level.getGameTime());
		for (Vec3 point : points) {
			Vec3 particlePos = pos.add(point);
			level.sendParticles(options, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0,
					clamped.speed());
		}
	}

	private static ParticleOptions options(ServerLevel level, GenericParticleTestConfig config) {
		ParticleColor color = config.randomColor() ? randomColor(level) : color(config.color());
		return switch (config.kind()) {
		case GLOW -> new ColorParticleData(HLParticleInit.glow.get(), color);
		case DARK_GLOW -> new DarkColorParticleData(HLParticleInit.dark_glow.get(), color);
		case EMBER -> new EmberParticleData(HLParticleInit.ember.get(), color, config.scale(), config.alpha(),
				config.life());
		};
	}

	private static ParticleColor color(int argb) {
		return new ParticleColor((argb >>> 16) & 0xFF, (argb >>> 8) & 0xFF, argb & 0xFF);
	}

	private static ParticleColor randomColor(ServerLevel level) {
		return new ParticleColor(level.random.nextInt(256), level.random.nextInt(256), level.random.nextInt(256));
	}

	static List<Vec3> shapePoints(GenericParticleTestConfig config, long gameTime) {
		GenericParticleTestConfig clamped = config.clamped();
		double rotMod = Math.max(0.05D, ((gameTime % 240L) + 1L) / 24.0D);
		double radius = Math.max(1.25D, clamped.spread());
		int count = Math.max(2, clamped.count());
		Vec3[] rawPoints = switch (clamped.shape()) {
		case BURST -> new Vec3[0];
		case FIBONACCI_SPHERE -> HLParticleUtils.fibboSphere(count, rotMod, radius);
		case RANDOM_SPHERE -> HLParticleUtils.randomSphere(count, rotMod, radius);
		case INVERSED_SPHERE -> HLParticleUtils.inversedSphere(count, rotMod, radius, false);
		case IMPLODE -> HLParticleUtils.implode(count, rotMod, radius, false);
		case LOTUS_FOUNTAIN -> HLParticleUtils.lotusFountain(count, rotMod, radius, false);
		case BLOOMING_FLOWER -> HLParticleUtils.bloomingFlower(count, rotMod, radius, false);
		case COSMIC_BIRTH -> HLParticleUtils.cosmicBirth(count, rotMod, radius, false);
		case COSMIC_BIRTH_INVERSE -> HLParticleUtils.cosmicBirthInverse(count, rotMod, radius, false);
		case SQUASH_STRETCH -> HLParticleUtils.squashAndStretch(count, rotMod, radius, false);
		case RANDOM_SWIMMING -> HLParticleUtils.randomSwimming(count, rotMod, radius, false);
		case TANGENT_FUNNEL -> HLParticleUtils.tangentFunnel(count, rotMod, radius, false);
		};
		List<Vec3> points = new ArrayList<>(rawPoints.length);
		for (Vec3 point : rawPoints) {
			if (point != null && isFinite(point)) {
				points.add(point);
			}
		}
		if (points.isEmpty() && clamped.shape() != GenericParticleTestConfig.Shape.BURST) {
			for (Vec3 point : HLParticleUtils.fibboSphere(count, rotMod, radius)) {
				if (point != null && isFinite(point)) {
					points.add(point);
				}
			}
		}
		return points;
	}

	private static boolean isFinite(Vec3 point) {
		return Double.isFinite(point.x) && Double.isFinite(point.y) && Double.isFinite(point.z);
	}
}
