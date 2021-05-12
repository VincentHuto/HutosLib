package com.hutoslib.common;

import com.hutoslib.HutosLib;
import com.hutoslib.client.models.AnimationPacket;
import com.hutoslib.client.particle.ParticleColor;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class HutosLibPacketHandler {
	private static int networkID = 0;
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel MAINCHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(HutosLib.MOD_ID, "animchannel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	public static void registerChannels() {
		// Register Networking packets
		MAINCHANNEL.messageBuilder(AnimationPacket.class, networkID++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(AnimationPacket::encode).decoder(AnimationPacket::new).consumer(AnimationPacket::handle).add();
		MAINCHANNEL.registerMessage(networkID++, PacketSpawnLightningParticle.class,
				PacketSpawnLightningParticle::encode, PacketSpawnLightningParticle::decode,
				PacketSpawnLightningParticle::handle);
	}

	/***
	 * 
	 * @param vec       Beginning Location
	 * @param endVec  	Ending location
	 * @param radius    How far to send the packet to
	 * @param dimension The dimension Key to send to
	 * @param color     Lightning Color
	 * @param speed     Speed in blocks/tick
	 * @param maxAge    How long it stays rendered
	 * @param fract     How much it Fractals out
	 * @param maxOff    How far each fractal can branch
	 */
	public static void sendLightningSpawn(Vector3d vec, Vector3d endVec, float radius, RegistryKey<World> dimension,
			ParticleColor color, int speed, int maxAge, int fract, float maxOff) {
		PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle(vec, endVec, color, speed, maxAge, fract,
				maxOff);
		MAINCHANNEL.send(PacketDistributor.NEAR
				.with(() -> new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, (double) radius, dimension)), msg);

		/*
		 * Proper Use Example
		 * 
		 * Vector3d translation = new Vector3d(0, 1, 0); Vector3d speedVec = new
		 * Vector3d(target.getPosition().getX(), target.getPosition().getY() +
		 * target.getHeight() / 2.0f, target.getPosition().getZ());
		 * 
		 * PacketHandler.sendLightningSpawn(player.getPositionVec().add(translation),
		 * speedVec, 64.0f, (RegistryKey<World>) player.world.getDimensionKey(),
		 * ParticleColor.YELLOW, 2, 10, 9, 0.2f);
		 */

	}
}
