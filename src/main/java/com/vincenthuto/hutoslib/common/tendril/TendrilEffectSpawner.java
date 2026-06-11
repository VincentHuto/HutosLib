package com.vincenthuto.hutoslib.common.tendril;

import java.util.Optional;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.common.network.PacketSpawnTendrilEffect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class TendrilEffectSpawner {
	private TendrilEffectSpawner() {
	}

	public static void spawn(ServerLevel level, TendrilAnchor start, TendrilAnchor end, TendrilEffectConfig config) {
		spawn(level, null, start, end, config);
	}

	public static void spawn(ServerLevel level, @Nullable ServerPlayer primaryPlayer, TendrilAnchor start,
			TendrilAnchor end, TendrilEffectConfig config) {
		TendrilEffectConfig clamped = config.clamped();
		TendrilAnchor.EntityResolver resolver = TendrilAnchor.forLevel(level);
		Optional<Vec3> startPos = start.resolve(resolver);
		Optional<Vec3> endPos = end.resolve(resolver);
		if (startPos.isEmpty() || endPos.isEmpty()) {
			return;
		}
		double radius = Math.max(32.0D, Math.min(192.0D, startPos.get().distanceTo(endPos.get()) + clamped.range()));
		PacketSpawnTendrilEffect packet = new PacketSpawnTendrilEffect(start, end, clamped);
		if (primaryPlayer != null) {
			PacketDistributor.sendToPlayer(primaryPlayer, packet);
		}
		PacketDistributor.sendToPlayersNear(level, primaryPlayer, startPos.get().x, startPos.get().y, startPos.get().z,
				radius, packet);
	}
}
