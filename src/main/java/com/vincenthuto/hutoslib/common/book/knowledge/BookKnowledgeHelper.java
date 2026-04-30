package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side helper that unlocks book entries registered in
 * {@link BookEntryRegistry} and syncs the result to the client.
 *
 * <p>All typed entry points delegate to the shared
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
     * Unlocks all book entries registered for {@code entityTypeId} in
     * {@link BookEntryRegistry} and syncs to the client if anything changed.
     *
     * @param player       the server-side player who made the kill
     * @param entityTypeId the registry key of the killed entity type
     */
    public static void unlockForEntityKill(ServerPlayer player, ResourceLocation entityTypeId) {
        Set<ResourceLocation> entries = BookEntryRegistry.entriesForEntityKill(entityTypeId);
        if (!entries.isEmpty()) {
            unlockEntries(player, entries, CommonDiscoverySource.ENTITY_KILL);
        }
    }

    /**
     * Unlocks all book entries registered for {@code biomeId} in
     * {@link BookEntryRegistry} and syncs to the client if anything changed.
     *
     * @param player  the server-side player
     * @param biomeId the registry key of the biome the player just entered
     */
    public static void unlockForBiome(ServerPlayer player, ResourceLocation biomeId) {
        Set<ResourceLocation> entries = BookEntryRegistry.entriesForBiome(biomeId);
        if (!entries.isEmpty()) {
            unlockEntries(player, entries, CommonDiscoverySource.BIOME_ENTER);
        }
    }

    /**
     * Unlocks all book entries registered for {@code structureId} in
     * {@link BookEntryRegistry} and syncs to the client if anything changed.
     *
     * @param player      the server-side player
     * @param structureId the registry key of the structure the player just entered
     */
    public static void unlockForStructure(ServerPlayer player, ResourceLocation structureId) {
        Set<ResourceLocation> entries = BookEntryRegistry.entriesForStructure(structureId);
        if (!entries.isEmpty()) {
            unlockEntries(player, entries, CommonDiscoverySource.STRUCTURE_DISCOVER);
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
