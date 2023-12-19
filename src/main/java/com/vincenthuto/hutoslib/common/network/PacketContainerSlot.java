package com.vincenthuto.hutoslib.common.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PacketContainerSlot
{
    public PacketContainerSlot()
    {
    }

    public PacketContainerSlot(FriendlyByteBuf buf)
    {
    }

    public void encode(FriendlyByteBuf buf)
    {
    }

	public boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().getSender().containerMenu.sendAllDataToRemote();
        return true;
    }
}
