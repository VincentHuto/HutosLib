package com.hutoslib.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class VanillaPacketDispatcher {

	public static void dispatchTEToNearbyPlayers(BlockEntity tile) {
		ClientboundBlockEntityDataPacket packet = tile.getUpdatePacket();
		BlockPos pos = tile.getBlockPos();

		if (packet != null && tile.getLevel() instanceof ServerLevel) {
			((ServerChunkCache) tile.getLevel().getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false)
					.forEach(e -> e.connection.send(packet));
		}
	}

	public static void dispatchTEToNearbyPlayers(Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile != null)
			dispatchTEToNearbyPlayers(tile);
	}

	public static float pointDistancePlane(double x1, double y1, double x2, double y2) {
		return (float) Math.hypot(x1 - x2, y1 - y2);
	}

}