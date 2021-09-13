package com.vincenthuto.hutoslib.client.model;

import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import com.vincenthuto.hutoslib.client.ClientUtils;
import com.vincenthuto.hutoslib.client.model.capability.Animation;
import com.vincenthuto.hutoslib.client.model.capability.IAnimatable;
import com.vincenthuto.hutoslib.common.network.HutosLibPacketHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class AnimationPacket {
	private final int entityID, animationIndex;

	public AnimationPacket(int entityID, int index) {
		this.entityID = entityID;
		this.animationIndex = index;
	}

	public AnimationPacket(FriendlyByteBuf buf) {
		entityID = buf.readInt();
		animationIndex = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(animationIndex);
	}

	public boolean handle(Supplier<NetworkEvent.Context> context) {
		return DistExecutor.unsafeCallWhenOn(Dist.CLIENT,
				() -> () -> ClientUtils.handleAnimationPacket(entityID, animationIndex));
	}

	public static <T extends Entity & IAnimatable> void send(T entity, Animation animation) {
		if (!entity.level.isClientSide) {
			entity.setAnimation(animation);
			HutosLibPacketHandler.MAINCHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
					new AnimationPacket(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)));
		}
	}
}