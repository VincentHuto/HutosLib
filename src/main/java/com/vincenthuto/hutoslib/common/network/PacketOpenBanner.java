package com.vincenthuto.hutoslib.common.network;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

public class PacketOpenBanner {

	public PacketOpenBanner() {
	}

	public PacketOpenBanner(FriendlyByteBuf buf) {
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public boolean handle(Supplier<NetworkEvent.Context> context) {
		context.get().getSender()
				.openMenu(new SimpleMenuProvider(
						(i, playerInventory, playerEntity) -> new BannerSlotContainer(i, playerInventory),
						Component.literal("container.crafting")));
		return true;
	}
}
