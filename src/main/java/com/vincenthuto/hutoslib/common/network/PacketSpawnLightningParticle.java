
package com.vincenthuto.hutoslib.common.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class PacketSpawnLightningParticle {

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

	public static void handle(PacketSpawnLightningParticle msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		Optional<?> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			return;
		}

		((ClientLevel) clientWorld.get()).addParticle(
				LightningParticleFactory.createData(msg.color, msg.getSpeed(), msg.maxAge, msg.fract,
						msg.getMaxOffset()),
				msg.getPosition().x, msg.getPosition().y, msg.getPosition().z, msg.getSpeedVec().x, msg.getSpeedVec().y,
				msg.getSpeedVec().z);
		ctxSupplier.get().setPacketHandled(true);
	}

	Vec3 startVec;
	Vec3 endVec;
	ParticleColor color;

	public float speed;

	public int maxAge, fract;

	public float maxOffset;

	public PacketSpawnLightningParticle() {
	}

	public PacketSpawnLightningParticle(Vec3 entVec, Vec3 endVec2, ParticleColor color, float s, int a, int f,
			float o) {
		this.startVec = new Vec3(entVec.x, entVec.y, entVec.z);
		this.endVec = new Vec3(endVec2.x, endVec2.y, endVec2.z);
		this.color = color;
		this.speed = s;
		this.maxAge = a;
		this.fract = f;
		this.maxOffset = o;
	}

	public ParticleColor getColor() {
		return color;
	}

	public int getFract() {
		return fract;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public float getMaxOffset() {
		return maxOffset;
	}

	public Vec3 getPosition() {
		return this.startVec;
	}

	public float getSpeed() {
		return speed;
	}

	public Vec3 getSpeedVec() {
		return this.endVec;
	}
}
