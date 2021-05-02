package com.hutoslib.util;

import com.hutoslib.client.models.IAnimatable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientUtils {

	public static Minecraft getClient() {
		return Minecraft.getInstance();
	}

	public static PlayerEntity getClientPlayer() {
		return getClient().player;
	}

	public static ClientWorld getWorld() {
		return getClient().world;
	}

	public static float getPartialTicks() {
		return getClient().getRenderPartialTicks();
	}

	public static boolean handleAnimationPacket(int entityID, int animationIndex) {
		World world = getWorld();
		IAnimatable entity = (IAnimatable) world.getEntityByID(entityID);

		if (animationIndex < 0)
			entity.setAnimation(IAnimatable.NO_ANIMATION);
		else
			entity.setAnimation(entity.getAnimations()[animationIndex]);
		return true;
	}
}
