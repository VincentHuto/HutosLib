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
(buf, msg) -> msg.encode(buf), PacketSpawnLightningParticle::new);

public static void handle(PacketSpawnLightningParticle msg, IPayloadContext ctx) {
ctx.enqueueWork(() -> {
ClientLevel level = Minecraft.getInstance().level;
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

public PacketSpawnLightningParticle(FriendlyByteBuf buf) {
try {
this.startVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
this.endVec = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
this.color = new ParticleColor(buf.readFloat(), buf.readFloat(), buf.readFloat());
this.speed = buf.readFloat();
this.maxAge = buf.readInt();
this.fract = buf.readInt();
this.maxOffset = buf.readFloat();
} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
this.startVec = Vec3.ZERO;
this.endVec = Vec3.ZERO;
this.color = new ParticleColor(1.0F, 1.0F, 1.0F);
this.speed = 0.0F;
this.maxAge = 0;
this.fract = 0;
this.maxOffset = 0.0F;
}
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

public void encode(FriendlyByteBuf buf) {
buf.writeDouble(getPosition().x);
buf.writeDouble(getPosition().y);
buf.writeDouble(getPosition().z);
buf.writeDouble(getSpeedVec().x);
buf.writeDouble(getSpeedVec().y);
buf.writeDouble(getSpeedVec().z);
buf.writeFloat(getColor().getRed());
buf.writeFloat(getColor().getGreen());
buf.writeFloat(getColor().getBlue());
buf.writeFloat(getSpeed());
buf.writeInt(getMaxAge());
buf.writeInt(getFract());
buf.writeFloat(getMaxOffset());
}

@Override
public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
return TYPE;
}
}
