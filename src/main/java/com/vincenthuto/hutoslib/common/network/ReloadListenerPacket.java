package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.shadow.PlaceboJsonReloadListener;
import com.vincenthuto.hutoslib.common.data.shadow.TypeKeyed;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class ReloadListenerPacket {

final String path;

public ReloadListenerPacket(String path) {
this.path = path;
}

public static class Start extends ReloadListenerPacket implements CustomPacketPayload {

public static final CustomPacketPayload.Type<Start> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("reload_listener_start"));

public static final StreamCodec<FriendlyByteBuf, Start> CODEC = StreamCodec.of(
(buf, msg) -> buf.writeUtf(msg.path, 50),
buf -> new Start(buf.readUtf(50)));

public Start(String path) {
super(path);
}

public static void handle(Start msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> PlaceboJsonReloadListener.initSync(msg.path));
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}

public static class Content<V extends TypeKeyed<V>> extends ReloadListenerPacket implements CustomPacketPayload {

@SuppressWarnings("rawtypes")
public static final CustomPacketPayload.Type<Content> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("reload_listener_content"));

@SuppressWarnings({ "rawtypes", "unchecked" })
public static final StreamCodec<FriendlyByteBuf, Content> CODEC = StreamCodec.of(
(buf, msg) -> {
buf.writeUtf(msg.path, 50);
buf.writeResourceLocation(msg.key);
PlaceboJsonReloadListener.writeItem(msg.path, (TypeKeyed<?>) msg.item, buf);
},
buf -> {
String path = buf.readUtf(50);
ResourceLocation key = buf.readResourceLocation();
Object item = PlaceboJsonReloadListener.readItem(path, key, buf);
return new Content<>(path, key, (TypeKeyed) item);
});

final ResourceLocation key;
final V item;

public Content(String path, ResourceLocation key, V item) {
super(path);
this.key = key;
this.item = item;
}

public static void handle(Content<?> msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> PlaceboJsonReloadListener.acceptItem(msg.path, msg.item));
}

@Override
@SuppressWarnings("rawtypes")
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}

public static class End extends ReloadListenerPacket implements CustomPacketPayload {

public static final CustomPacketPayload.Type<End> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("reload_listener_end"));

public static final StreamCodec<FriendlyByteBuf, End> CODEC = StreamCodec.of(
(buf, msg) -> buf.writeUtf(msg.path, 50),
buf -> new End(buf.readUtf(50)));

public End(String path) {
super(path);
}

public static void handle(End msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> PlaceboJsonReloadListener.endSync(msg.path));
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
}
