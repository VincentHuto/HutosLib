package com.vincenthuto.hutoslib.common.lightning;

import com.vincenthuto.hutoslib.common.network.PacketSpawnLightningTest;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class LightningTesterSpawner {

	private LightningTesterSpawner() {
	}

	public static void spawn(ServerLevel level, Vec3 start, Vec3 end, LightningTestConfig config) {
		spawn(level, null, start, end, config);
	}

	public static void spawn(ServerLevel level, @Nullable ServerPlayer primaryPlayer, Vec3 start, Vec3 end,
			LightningTestConfig config) {
		LightningTestConfig clamped = config.clamped();
		double radius = Math.max(32.0D, Math.min(192.0D, start.distanceTo(end) + clamped.range()));
		PacketSpawnLightningTest packet = new PacketSpawnLightningTest(start, end, clamped);
		if (primaryPlayer != null) {
			PacketDistributor.sendToPlayer(primaryPlayer, packet);
		}
		PacketDistributor.sendToPlayersNear(level, primaryPlayer, start.x, start.y, start.z, radius, packet);
	}
}
