package com.vincenthuto.hutoslib.common.book.knowledge;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.advancements.AdvancementEarnEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

/**
 * Server-side event listener that automatically unlocks book entries
 * registered in {@link BookEntryRegistry} when a player picks up an item or
 * earns an advancement.
 *
 * <p>Any mod that calls {@link BookEntryRegistry#registerItemUnlock} or
 * {@link BookEntryRegistry#registerAdvancementUnlock} during setup will have
 * its entries unlocked automatically by these listeners — no additional event
 * code is required in the consuming mod.
 */
@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class BookDiscoveryEvents {

    private BookDiscoveryEvents() {
    }

    /**
     * Fires when a player picks up an item entity. Looks up the item's
     * registry ID in {@link BookEntryRegistry} and unlocks any registered
     * entries via {@link BookKnowledgeHelper}.
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getOriginalStack().getItem());
        if (itemId == null) {
            return;
        }
        BookKnowledgeHelper.unlockForItemPickup(player, itemId);
    }

    /**
     * Fires when a player earns an advancement. Looks up the advancement ID in
     * {@link BookEntryRegistry} and unlocks any registered entries via
     * {@link BookKnowledgeHelper}.
     */
    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation advancementId = event.getAdvancement().id();
        BookKnowledgeHelper.unlockForAdvancement(player, advancementId);
    }
}
