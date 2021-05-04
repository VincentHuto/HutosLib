package com.hutoslib.client.models;

import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import com.hutoslib.common.PacketHandler;
import com.hutoslib.util.ClientUtils;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class AnimationPacket {
	private final int entityID, animationIndex;

	public AnimationPacket(int entityID, int index) {
		this.entityID = entityID;
		this.animationIndex = index;
	}

	public AnimationPacket(PacketBuffer buf) {
		entityID = buf.readInt();
		animationIndex = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(entityID);
		buf.writeInt(animationIndex);
	}

	public boolean handle(Supplier<NetworkEvent.Context> context) {
		return DistExecutor.unsafeCallWhenOn(Dist.CLIENT,
				() -> () -> ClientUtils.handleAnimationPacket(entityID, animationIndex));
	}

	public static <T extends Entity & IAnimatable> void send(T entity, Animation animation) {
		if (!entity.world.isRemote) {
			entity.setAnimation(animation);
			PacketHandler.MAINCHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
					new AnimationPacket(entity.getEntityId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
		}
	}
}