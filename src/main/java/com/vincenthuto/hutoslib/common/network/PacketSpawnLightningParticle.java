package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketSpawnLightningParticle implements CustomPacketPayload {

public static final CustomPacketPayload.Type<PacketSpawnLightningParticle> TYPE =
new CustomPacketPayload.Type<>(HutosLib.rloc("packet_spawn_lightning"));

public static final StreamCodec<FriendlyByteBuf, PacketSpawnLightningParticle> CODEC = StreamCodec.of(
PacketSpawnLightningParticle::encode, PacketSpawnLightningParticle::decode);

public static PacketSpawnLightningParticle decode(FriendlyByteBuf buf) {
PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle();
try {
msg.startVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
msg.endVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
msg.color = new ParticleColor(buf.readFloat(), buf.readFloat(), buf.readFloat());
msg.speed = buf.readFloat();
msg.maxAge = buf.readInt();
msg.fract = buf.readInt();
msg.maxOffset = buf.readFloat();
} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
return msg;
}
return msg;
}

public static void encode(PacketSpawnLightningParticle msg, FriendlyByteBuf buf) {
buf.writeDouble(msg.getPosition().x);
buf.writeDouble(msg.getPosition().y);
buf.writeDouble(msg.getPosition().z);
buf.writeDouble(msg.getSpeedVec().x);
buf.writeDouble(msg.getSpeedVec().y);
buf.writeDouble(msg.getSpeedVec().z);
buf.writeFloat(msg.getColor().getRed());
buf.writeFloat(msg.getColor().getGreen());
buf.writeFloat(msg.getColor().getBlue());
buf.writeFloat(msg.getSpeed());
buf.writeInt(msg.getMaxAge());
buf.writeInt(msg.getFract());
buf.writeFloat(msg.getMaxOffset());
}

public static void handle(PacketSpawnLightningParticle msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
ClientLevel level = (ClientLevel) Minecraft.getInstance().level;
if (level == null) return;
level.addParticle(
LightningParticleFactory.createData(msg.color, msg.getSpeed(), msg.maxAge, msg.fract, msg.getMaxOffset()),
msg.getPosition().x, msg.getPosition().y, msg.getPosition().z,
msg.getSpeedVec().x, msg.getSpeedVec().y, msg.getSpeedVec().z);
});
}

Vec3 startVec;
Vec3 endVec;
ParticleColor color;
public float speed;
public int maxAge, fract;
public float maxOffset;

public PacketSpawnLightningParticle() {
}

public PacketSpawnLightningParticle(Vec3 entVec, Vec3 endVec2, ParticleColor color, float s, int a, int f, float o) {
this.startVec = new Vec3(entVec.x, entVec.y, entVec.z);
this.endVec = new Vec3(endVec2.x, endVec2.y, endVec2.z);
this.color = color;
this.speed = s;
this.maxAge = a;
this.fract = f;
this.maxOffset = o;
}

public ParticleColor getColor() { return color; }
public int getFract() { return fract; }
public int getMaxAge() { return maxAge; }
public float getMaxOffset() { return maxOffset; }
public Vec3 getPosition() { return this.startVec; }
public float getSpeed() { return speed; }
public Vec3 getSpeedVec() { return this.endVec; }

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
