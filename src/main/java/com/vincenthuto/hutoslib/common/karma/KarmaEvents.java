package com.vincenthuto.hutoslib.common.karma;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketKarmaServer;
import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.NEOFORGE)
public class KarmaEvents {

@SubscribeEvent
public static void onDimensionChange(PlayerChangedDimensionEvent event) {
ServerPlayer player = (ServerPlayer) event.getEntity();
IKarma volume = player.getData(HLAttachmentTypes.KARMA.get());
PacketDistributor.sendToPlayer(player, new PacketKarmaServer(volume));
}

@SubscribeEvent
public static void playerDeath(PlayerEvent.Clone event) {
if (event.isWasDeath()) {
Player peorig = event.getOriginal();
IKarma bloodVolumeOld = peorig.getData(HLAttachmentTypes.KARMA.get());
Player playernew = event.getEntity();
IKarma bloodVolumeNew = playernew.getData(HLAttachmentTypes.KARMA.get());
bloodVolumeNew.setActive(bloodVolumeOld.isActive());
bloodVolumeNew.setKarma(bloodVolumeOld.getKarma());
}
}

@SubscribeEvent
public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
ServerPlayer player = (ServerPlayer) event.getEntity();
IKarma volume = player.getData(HLAttachmentTypes.KARMA.get());
PacketDistributor.sendToPlayer(player, new PacketKarmaServer(volume));
}

@SubscribeEvent
public static void playerRespawn(PlayerRespawnEvent event) {
Player playernew = event.getEntity();
if (!playernew.level().isClientSide) {
IKarma bloodVolumeNew = playernew.getData(HLAttachmentTypes.KARMA.get());
PacketDistributor.sendToPlayer((ServerPlayer) playernew,
new PacketKarmaServer(bloodVolumeNew.isActive(), bloodVolumeNew.getKarma()));
}
}
}
