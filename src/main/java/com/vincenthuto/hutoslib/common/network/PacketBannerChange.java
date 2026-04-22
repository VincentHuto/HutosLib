package com.vincenthuto.hutoslib.common.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketBannerChange implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketBannerChange> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_banner_change"));

public static final StreamCodec<RegistryFriendlyByteBuf, PacketBannerChange> CODEC = StreamCodec.of(
(buf, msg) -> msg.encode(buf), PacketBannerChange::new);

public int player;
public String where;
public JsonElement slot;
public ItemStack stack;

public PacketBannerChange(RegistryFriendlyByteBuf buf) {
player = buf.readVarInt();
where = buf.readUtf();
slot = JsonParser.parseString(buf.readUtf(2048));
stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
}

public PacketBannerChange(LivingEntity player, String where, JsonElement slot, ItemStack stack) {
this.player = player.getId();
this.where = where;
this.slot = slot;
this.stack = stack.copy();
}

public void encode(RegistryFriendlyByteBuf buf) {
buf.writeVarInt(player);
buf.writeUtf(where);
buf.writeUtf(slot.toString(), 2048);
ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
}

public static void handle(PacketBannerChange msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
Minecraft minecraft = Minecraft.getInstance();
Entity entity = minecraft.level.getEntity(msg.player);
if (!(entity instanceof Player)) return;
BannerFinder.setBannerFromPacket((Player) entity, msg.where, msg.slot, msg.stack);
});
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
