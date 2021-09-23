package com.vincenthuto.hutoslib.common.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ContainerSlotsHack
{
    public ContainerSlotsHack()
    {
    }

    public ContainerSlotsHack(FriendlyByteBuf buf)
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
