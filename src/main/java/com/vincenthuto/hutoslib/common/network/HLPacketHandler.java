package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class HLPacketHandler {
	private static int networkID = 0;
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel MAINCHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(HutosLib.MOD_ID, "mainchannel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	public static void registerChannels() {
		/*
		 * // Register Networking packets
		 * MAINCHANNEL.messageBuilder(AnimationPacket.class, networkID++,
		 * NetworkDirection.PLAY_TO_CLIENT)
		 * .encoder(AnimationPacket::encode).decoder(AnimationPacket::new).consumerNetworkThread(
		 * AnimationPacket::handle).add();
		 */
		MAINCHANNEL.registerMessage(networkID++, PacketSpawnLightningParticle.class,
				PacketSpawnLightningParticle::encode, PacketSpawnLightningParticle::decode,
				PacketSpawnLightningParticle::handle);

		MAINCHANNEL.messageBuilder(PacketSyncBannerSlotContents.class, networkID++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketSyncBannerSlotContents::encode).decoder(PacketSyncBannerSlotContents::new)
				.consumerNetworkThread(PacketSyncBannerSlotContents::handle).add();

		MAINCHANNEL.messageBuilder(PacketOpenBanner.class, networkID++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketOpenBanner::encode).decoder(PacketOpenBanner::new).consumerNetworkThread(PacketOpenBanner::handle)
				.add();

		MAINCHANNEL.messageBuilder(PacketOpenBanner.class, networkID++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketOpenBanner::encode).decoder(PacketOpenBanner::new).consumerNetworkThread(PacketOpenBanner::handle)
				.add();

		MAINCHANNEL.messageBuilder(PacketContainerSlot.class, networkID++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketContainerSlot::encode).decoder(PacketContainerSlot::new)
				.consumerNetworkThread(PacketContainerSlot::handle).add();

		MAINCHANNEL.messageBuilder(PacketBannerChange.class, networkID++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketBannerChange::encode).decoder(PacketBannerChange::new)
				.consumerNetworkThread(PacketBannerChange::handle).add();

		MAINCHANNEL.registerMessage(networkID++, PacketKarmaClient.class, PacketKarmaClient::encode,
				PacketKarmaClient::decode, PacketKarmaClient::handle);
		MAINCHANNEL.registerMessage(networkID++, PacketKarmaServer.class, PacketKarmaServer::encode,
				PacketKarmaServer::decode, PacketKarmaServer::handle);

		/*
		 * MAINCHANNEL.registerMessage(networkID++, PacketUpdateSOHItem.class,
		 * PacketUpdateSOHItem::encode, PacketUpdateSOHItem::decode,
		 * PacketUpdateSOHItem.Handler::handle);
		 */
	}

	/***
	 *
	 * @param entVec    Beginning Location
	 * @param endVec    Ending location
	 * @param radius    How far to send the packet to
	 * @param dimension The dimension Key to send to
	 * @param color     Lightning Color
	 * @param speed     Speed in blocks/tick
	 * @param maxAge    How long it stays rendered
	 * @param fract     How much it Fractals out
	 * @param maxOff    How far each fractal can branch
	 */
	public static void sendLightningSpawn(Vec3 entVec, Vec3 endVec, float radius, ResourceKey<Level> dimension,
			ParticleColor color, float speed, int maxAge, int fract, float maxOff) {
		PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle(entVec, endVec, color, speed, maxAge, fract,
				maxOff);
		MAINCHANNEL.send(PacketDistributor.NEAR.with(
				() -> new PacketDistributor.TargetPoint(entVec.x, entVec.y, entVec.z, radius, dimension)),
				msg);

	}
}
