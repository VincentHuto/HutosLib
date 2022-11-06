package com.vincenthuto.hutoslib.common.karma;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketKarmaServer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class KarmaEvents {
	@SubscribeEvent
	public static void attachCapabilitiesEntity(final AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			KarmaProvider provider = new KarmaProvider();
			event.addCapability(new ResourceLocation(HutosLib.MOD_ID, "karma"), provider);
		}
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerChangedDimensionEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();
		IKarma volume = player.getCapability(KarmaProvider.KARMA_CAPA).orElseThrow(NullPointerException::new);
		HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketKarmaServer(volume));
	//	player.displayClientMessage(
	//			 Component.literal("Welcome! Current Karma: " + ChatFormatting.GOLD + volume.getKarma() + "ml"),
	//			false);
	}

	@SubscribeEvent
	public static void playerDeath(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			Player peorig = event.getOriginal();
			peorig.revive();
			IKarma bloodVolumeOld = peorig.getCapability(KarmaProvider.KARMA_CAPA)
					.orElseThrow(NullPointerException::new);
			Player playernew = event.getEntity();
			peorig.reviveCaps();
			IKarma bloodVolumeNew = playernew.getCapability(KarmaProvider.KARMA_CAPA)
					.orElseThrow(NullPointerException::new);
			bloodVolumeNew.setActive(bloodVolumeOld.isActive());
			bloodVolumeNew.setKarma(bloodVolumeOld.getKarma());
			peorig.invalidateCaps();
		}

	}

	@SubscribeEvent
	public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayer player = (ServerPlayer) event.getEntity();
		IKarma volume = player.getCapability(KarmaProvider.KARMA_CAPA).orElseThrow(NullPointerException::new);
		HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketKarmaServer(volume));
//		player.displayClientMessage(
//				 Component.literal("Welcome! Karma Active? " + ChatFormatting.LIGHT_PURPLE + volume.isActive()), false);
//		player.displayClientMessage(
//				 Component.literal("Welcome! Current Karma: " + ChatFormatting.GOLD + volume.getKarma() + "ml"),
//				false);
	}

	@SubscribeEvent
	public static void playerRespawn(PlayerRespawnEvent event) {
		Player playernew = event.getEntity();
		if (!playernew.level.isClientSide) {
			IKarma bloodVolumeNew = playernew.getCapability(KarmaProvider.KARMA_CAPA)
					.orElseThrow(NullPointerException::new);
			HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playernew),
					new PacketKarmaServer(bloodVolumeNew.isActive(), bloodVolumeNew.getKarma()));
		}
	}

}
