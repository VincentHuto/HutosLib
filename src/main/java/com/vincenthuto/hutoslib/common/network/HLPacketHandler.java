package com.vincenthuto.hutoslib.common.network;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class HLPacketHandler {

@SubscribeEvent
public static void registerPayloads(RegisterPayloadHandlersEvent event) {
PayloadRegistrar registrar = event.registrar(HutosLib.MOD_ID).versioned("1");

registrar.playToClient(ReloadListenerPacket.Start.TYPE, ReloadListenerPacket.Start.CODEC,
ReloadListenerPacket.Start::handle);
registrar.playToClient(ReloadListenerPacket.Content.TYPE, ReloadListenerPacket.Content.CODEC,
ReloadListenerPacket.Content::handle);
registrar.playToClient(ReloadListenerPacket.End.TYPE, ReloadListenerPacket.End.CODEC,
ReloadListenerPacket.End::handle);

registrar.playBidirectional(PacketSpawnLightningParticle.TYPE, PacketSpawnLightningParticle.CODEC,
PacketSpawnLightningParticle::handle);
registrar.playToClient(PacketSpawnLightningTest.TYPE, PacketSpawnLightningTest.CODEC,
PacketSpawnLightningTest::handle);
registrar.playToClient(PacketSpawnTendrilEffect.TYPE, PacketSpawnTendrilEffect.CODEC,
PacketSpawnTendrilEffect::handle);
registrar.playToServer(PacketLightningTesterItem.TYPE, PacketLightningTesterItem.CODEC,
PacketLightningTesterItem::handle);
registrar.playToServer(PacketLightningTesterBlock.TYPE, PacketLightningTesterBlock.CODEC,
PacketLightningTesterBlock::handle);
registrar.playToServer(PacketTendrilTesterItem.TYPE, PacketTendrilTesterItem.CODEC,
PacketTendrilTesterItem::handle);
registrar.playToServer(PacketTendrilTesterBlock.TYPE, PacketTendrilTesterBlock.CODEC,
PacketTendrilTesterBlock::handle);
registrar.playToServer(PacketEffectTemplateItem.TYPE, PacketEffectTemplateItem.CODEC,
PacketEffectTemplateItem::handle);
registrar.playToServer(PacketGenericParticleTesterItem.TYPE, PacketGenericParticleTesterItem.CODEC,
PacketGenericParticleTesterItem::handle);
registrar.playToServer(PacketGenericParticleTesterBlock.TYPE, PacketGenericParticleTesterBlock.CODEC,
PacketGenericParticleTesterBlock::handle);
registrar.playToClient(PacketSyncBannerSlotContents.TYPE, PacketSyncBannerSlotContents.CODEC,
PacketSyncBannerSlotContents::handle);
registrar.playToServer(PacketOpenBanner.TYPE, PacketOpenBanner.CODEC,
PacketOpenBanner::handle);
registrar.playToServer(PacketContainerSlot.TYPE, PacketContainerSlot.CODEC,
PacketContainerSlot::handle);
registrar.playToClient(PacketBannerChange.TYPE, PacketBannerChange.CODEC,
PacketBannerChange::handle);
registrar.playToClient(PacketKarmaServer.TYPE, PacketKarmaServer.CODEC,
PacketKarmaServer::handle);
registrar.playToServer(PacketKarmaClient.TYPE, PacketKarmaClient.CODEC,
PacketKarmaClient::handle);

registrar.playToClient(PacketSyncBookKnowledge.TYPE, PacketSyncBookKnowledge.CODEC,
PacketSyncBookKnowledge::handle);
}

public static void sendLightningSpawn(Vec3 entVec, Vec3 endVec, float radius, ResourceKey<Level> dimension,
ParticleColor color, float speed, int maxAge, int fract, float maxOff) {
PacketSpawnLightningParticle msg = new PacketSpawnLightningParticle(entVec, endVec, color, speed, maxAge, fract, maxOff);
var server = ServerLifecycleHooks.getCurrentServer();
ServerLevel serverLevel = server != null ? server.getLevel(dimension) : null;
PacketDistributor.sendToPlayersNear(serverLevel, null, entVec.x, entVec.y, entVec.z, radius, msg);
}

public static void sendTo(CustomPacketPayload packet, Player player) {
PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
}

public static void sendToAll(CustomPacketPayload packet) {
PacketDistributor.sendToAllPlayers(packet);
}
}
