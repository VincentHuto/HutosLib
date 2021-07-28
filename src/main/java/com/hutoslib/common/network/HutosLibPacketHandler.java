package com.hutoslib.common.network;

import com.hutoslib.HutosLib;
import com.hutoslib.client.particle.util.ParticleColor;
import com.hutoslib.math.Vector3;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class HutosLibPacketHandler {
	private static int networkID = 0;
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel MAINCHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(HutosLib.MOD_ID, "animchannel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	public static void registerChannels() {
		/*
		 * // Register Networking packets
		 * MAINCHANNEL.messageBuilder(AnimationPacket.class, networkID++,
		 * NetworkDirection.PLAY_TO_CLIENT)
		 * .encoder(AnimationPacket::encode).decoder(AnimationPacket::new).consumer(
		 * AnimationPacket::handle).add();
		 */
		MAINCHANNEL.registerMessage(networkID++, PacketSpawnLightningParticle.class,
				PacketSpawnLightningParticle::encode, PacketSpawnLightningParticle::decode,
				PacketSpawnLightningParticle::handle);
		/*
		 * MAINCHANNEL.registerMessage(networkID++, PacketUpdateSOHItem.class,
		 * PacketUpdateSOHItem::encode, PacketUpdateSOHItem::decode,
		 * PacketUpdateSOHItem.Handler::handle);
		 */
	}

	/***
	 * 
	 * @param entVec       Beginning Location
	 * @param endVec    Ending location
	 * @param radius    How far to send the packet to
	 * @param dimension The dimension Key to send to
	 * @param color     Lightning Color
	 * @param speed     Speed in blocks/tick
	 * @param maxAge    How long it stays rendered
	 * @param fract     How much it Fractals out
	 * @param maxOff    How far each fractal can branch
	 */
	public static void sendLightningSpawn(Vector3 entVec, Vector3 endVec, float radius, ResourceKey<Level> dimension,
			ParticleColor color, float speed, int maxAge, int fract, float maxOff) {
		PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle(entVec, endVec, color, speed, maxAge, fract,
				maxOff);
		MAINCHANNEL.send(PacketDistributor.NEAR
				.with(() -> new PacketDistributor.TargetPoint(entVec.x, entVec.y, entVec.z, (double) radius, dimension)), msg);

	}
}
