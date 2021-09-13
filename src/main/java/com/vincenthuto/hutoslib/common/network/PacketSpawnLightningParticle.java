
package com.vincenthuto.hutoslib.common.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class PacketSpawnLightningParticle {
	Vec3 startVec;
	Vec3 endVec;
	ParticleColor color;
	public float speed;
	public int maxAge, fract;
	public float maxOffset;

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

	public PacketSpawnLightningParticle() {
	}

	public float getSpeed() {
		return speed;
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

	public Vec3 getSpeedVec() {
		return this.endVec;
	}

	public ParticleColor getColor() {
		return color;
	}

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
		buf.writeDouble((double) msg.getPosition().x);
		buf.writeDouble((double) msg.getPosition().y);
		buf.writeDouble((double) msg.getPosition().z);
		buf.writeDouble((double) msg.getSpeedVec().x);
		buf.writeDouble((double) msg.getSpeedVec().y);
		buf.writeDouble((double) msg.getSpeedVec().z);
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
		Optional<?> clientWorld = (Optional<?>) LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			return;
		}

		((ClientLevel) clientWorld.get()).addParticle(
				LightningParticleFactory.createData(new ParticleColor(1, 2, 3), msg.getSpeed(), msg.maxAge, msg.fract,
						msg.getMaxOffset()),
				(double) msg.getPosition().x, (double) msg.getPosition().y, (double) msg.getPosition().z,
				(double) msg.getSpeedVec().x, (double) msg.getSpeedVec().y, (double) msg.getSpeedVec().z);
		ctxSupplier.get().setPacketHandled(true);
	}
}
