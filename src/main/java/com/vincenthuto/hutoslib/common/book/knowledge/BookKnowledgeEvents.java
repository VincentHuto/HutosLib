package com.vincenthuto.hutoslib.common.book.knowledge;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.PacketSyncBookKnowledge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BookKnowledgeEvents {

    @SubscribeEvent
    public static void onDimensionChange(PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendSync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            BookKnowledge oldKnowledge = BookKnowledgeProvider.get(event.getOriginal());
            BookKnowledge newKnowledge = BookKnowledgeProvider.get(event.getEntity());
            newKnowledge.setFrom(oldKnowledge);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendSync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendSync(player);
        }
    }

    private static void sendSync(ServerPlayer player) {
        BookKnowledge knowledge = BookKnowledgeProvider.get(player);
        PacketDistributor.sendToPlayer(player,
                new PacketSyncBookKnowledge(player.getUUID(), knowledge, player.level().registryAccess()));
    }
}
