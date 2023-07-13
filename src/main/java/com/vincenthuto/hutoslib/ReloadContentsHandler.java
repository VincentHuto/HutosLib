package com.vincenthuto.hutoslib;

import com.vincenthuto.hutoslib.common.network.PacketSyncBookData;

import net.minecraft.server.MinecraftServer;

public class ReloadContentsHandler {
	public static void dataReloaded() {
		// Also reload contents when someone types /reload
		HutosLib.LOGGER.info("Sending reload packet to clients");
		PacketSyncBookData.sendToAll();
	}
}
