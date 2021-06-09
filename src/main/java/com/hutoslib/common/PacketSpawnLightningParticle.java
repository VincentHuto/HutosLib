
package com.hutoslib.common;

import java.util.Optional;
import java.util.function.Supplier;

import com.hutoslib.client.particle.factory.LightningParticleFactory;
import com.hutoslib.client.particle.util.ParticleColor;
import com.hutoslib.math.Vector3;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSpawnLightningParticle {
	Vector3 startVec;
	Vector3 endVec;
	ParticleColor color;
	public float speed;
	public int maxAge, fract;
	public float maxOffset;

	public PacketSpawnLightningParticle(Vector3d startVec, Vector3d endVec, ParticleColor color, float s, int a, int f,
			float o) {
		this.startVec = new Vector3(startVec.x, startVec.y, startVec.z);
		this.endVec = new Vector3(endVec.x, endVec.y, endVec.z);
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

	public Vector3 getPosition() {
		return this.startVec;
	}

	public Vector3 getSpeedVec() {
		return this.endVec;
	}

	public ParticleColor getColor() {
		return color;
	}

	public static PacketSpawnLightningParticle decode(PacketBuffer buf) {
		PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle();
		try {
			msg.startVec = new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			msg.endVec = new Vector3(buf.readDouble(), buf.readDouble(), buf.readDouble());
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

	public static void encode(PacketSpawnLightningParticle msg, PacketBuffer buf) {
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

		((ClientWorld) clientWorld.get()).addParticle(
				LightningParticleFactory.createData(msg.color, msg.getSpeed(), msg.maxAge, msg.fract,
						msg.getMaxOffset()),
				(double) msg.getPosition().x, (double) msg.getPosition().y, (double) msg.getPosition().z,
				(double) msg.getSpeedVec().x, (double) msg.getSpeedVec().y, (double) msg.getSpeedVec().z);
		ctxSupplier.get().setPacketHandled(true);
	}
}
