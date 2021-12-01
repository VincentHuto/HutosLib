package com.vincenthuto.hutoslib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;

public class ClientUtils {

	public static Minecraft getClient() {
		return Minecraft.getInstance();
	}

	public static Player getClientPlayer() {
		return getClient().player;
	}

	public static ClientLevel getWorld() {
		return getClient().level;
	}

	public static float getPartialTicks() {
		return getClient().getFrameTime();
	}

}
