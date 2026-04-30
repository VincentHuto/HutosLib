package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side helper that unlocks book entries registered in
 * {@link BookEntryRegistry} and syncs the result to the client.
 *
 * <p>Both public entry points delegate to the shared
 * {@link #unlockEntries(ServerPlayer, Set, IDiscoverySource)} core, which
 * performs the unlock loop and fires a client sync only when at least one
 * entry was newly added.
 *
 * <p>This class is intentionally stateless; call its methods from any
 * server-side context where you have a {@link ServerPlayer}.
 */
public final class BookKnowledgeHelper {

    private BookKnowledgeHelper() {
    }

    /**
     * Unlocks all book entries registered for {@code itemId} in
     * {@link BookEntryRegistry} and syncs to the client if anything changed.
     *
     * @param player the server-side player
     * @param itemId the registry key of the picked-up item
     */
    public static void unlockForItemPickup(ServerPlayer player, ResourceLocation itemId) {
        Set<ResourceLocation> entries = BookEntryRegistry.entriesForItem(itemId);
        if (!entries.isEmpty()) {
            unlockEntries(player, entries, CommonDiscoverySource.ITEM_PICKUP);
        }
    }

    /**
     * Unlocks all book entries registered for {@code advancementId} in
     * {@link BookEntryRegistry} and syncs to the client if anything changed.
     *
     * @param player        the server-side player
     * @param advancementId the advancement that was just earned
     */
    public static void unlockForAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        Set<ResourceLocation> entries = BookEntryRegistry.entriesForAdvancement(advancementId);
        if (!entries.isEmpty()) {
            unlockEntries(player, entries, CommonDiscoverySource.ADVANCEMENT);
        }
    }

    /**
     * Unlocks every entry in {@code entryIds} for {@code player}, recording
     * {@code source} as the discovery cause, then syncs to the client if any
     * entry was newly added.
     *
     * <p>This method is public so that mods can invoke it directly when they
     * have a pre-built set of entry IDs and a custom {@link IDiscoverySource}.
     *
     * @param player   the server-side player
     * @param entryIds entry IDs to unlock; must not be null
     * @param source   the discovery source to record against each entry
     */
    public static void unlockEntries(ServerPlayer player, Set<ResourceLocation> entryIds,
            IDiscoverySource source) {
        BookKnowledge knowledge = BookKnowledgeProvider.get(player);
        boolean changed = false;
        for (ResourceLocation entryId : entryIds) {
            if (knowledge.unlockEntry(entryId, source)) {
                changed = true;
            }
        }
        if (changed) {
            BookKnowledgeEvents.sync(player);
        }
    }
}
