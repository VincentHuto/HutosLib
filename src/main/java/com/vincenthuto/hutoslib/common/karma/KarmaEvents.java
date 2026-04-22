package com.vincenthuto.hutoslib.common.karma;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketKarmaServer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class KarmaEvents {

@SubscribeEvent
public static void onDimensionChange(PlayerChangedDimensionEvent event) {
if (event.getEntity() instanceof ServerPlayer player) {
IKarma karmaState = KarmaProvider.getKarma(player);
PacketDistributor.sendToPlayer(player, new PacketKarmaServer(karmaState));
}
}

@SubscribeEvent
public static void playerDeath(PlayerEvent.Clone event) {
if (event.isWasDeath()) {
Player peorig = event.getOriginal();
IKarma karmaStateOld = KarmaProvider.getKarma(peorig);
Player playernew = event.getEntity();
IKarma karmaStateNew = KarmaProvider.getKarma(playernew);
karmaStateNew.setActive(karmaStateOld.isActive());
karmaStateNew.setKarma(karmaStateOld.getKarma());
}
}

@SubscribeEvent
public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
if (event.getEntity() instanceof ServerPlayer player) {
IKarma karmaState = KarmaProvider.getKarma(player);
PacketDistributor.sendToPlayer(player, new PacketKarmaServer(karmaState));
}
}

@SubscribeEvent
public static void playerRespawn(PlayerRespawnEvent event) {
if (event.getEntity() instanceof ServerPlayer player) {
IKarma karmaState = KarmaProvider.getKarma(player);
PacketDistributor.sendToPlayer(player, new PacketKarmaServer(karmaState.isActive(), karmaState.getKarma()));
}
}
}
