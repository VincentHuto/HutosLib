package com.hutoslib.client;

import com.hutoslib.client.model.IAnimatable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

	public static boolean handleAnimationPacket(int entityID, int animationIndex) {
		Level world = getWorld();
		IAnimatable entity = (IAnimatable) world.getEntity(entityID);

		if (animationIndex < 0)
			entity.setAnimation(IAnimatable.NO_ANIMATION);
		else
			entity.setAnimation(entity.getAnimations()[animationIndex]);
		return true;
	}
}
