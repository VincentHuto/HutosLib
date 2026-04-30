package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;

/**
 * Central registry that maps item and advancement IDs to the book-entry
 * {@link ResourceLocation}s that should be unlocked when a player picks up
 * that item or earns that advancement.
 *
 * <p>Mods populate this registry during {@code FMLCommonSetupEvent} (or any
 * time before the first gameplay event fires):
 * <pre>{@code
 * BookEntryRegistry.registerItemUnlock(
 *         ResourceLocation.fromNamespaceAndPath("mymod", "my_item"),
 *         ResourceLocation.fromNamespaceAndPath("mymod", "guide/chapter"));
 *
 * BookEntryRegistry.registerAdvancementUnlock(
 *         ResourceLocation.fromNamespaceAndPath("mymod", "advancements/root"),
 *         ResourceLocation.fromNamespaceAndPath("mymod", "guide/chapter"));
 * }</pre>
 *
 * <p>HutosLib's {@link BookDiscoveryEvents} listens to item-pickup and
 * advancement events on the game bus and consults this registry automatically,
 * so mods only need to register their mappings here — no additional event
 * listener is required.
 */
public final class BookEntryRegistry {

    /** item registry ID → set of entry IDs to unlock on pickup */
    private static final Map<ResourceLocation, Set<ResourceLocation>> ITEM_UNLOCKS =
            new ConcurrentHashMap<>();

    /** advancement ID → set of entry IDs to unlock when earned */
    private static final Map<ResourceLocation, Set<ResourceLocation>> ADVANCEMENT_UNLOCKS =
            new ConcurrentHashMap<>();

    private BookEntryRegistry() {
    }

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    /**
     * Registers that picking up the item identified by {@code itemId} should
     * unlock the book entry {@code entryId}.
     *
     * @param itemId  registry key of the item (e.g. {@code minecraft:diamond})
     * @param entryId the book entry to unlock
     */
    public static void registerItemUnlock(ResourceLocation itemId, ResourceLocation entryId) {
        ITEM_UNLOCKS.computeIfAbsent(itemId, k -> ConcurrentHashMap.newKeySet()).add(entryId);
    }

    /**
     * Registers that earning the advancement identified by {@code advancementId}
     * should unlock the book entry {@code entryId}.
     *
     * @param advancementId the advancement's resource location
     * @param entryId       the book entry to unlock
     */
    public static void registerAdvancementUnlock(ResourceLocation advancementId,
            ResourceLocation entryId) {
        ADVANCEMENT_UNLOCKS.computeIfAbsent(advancementId, k -> ConcurrentHashMap.newKeySet())
                .add(entryId);
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    /**
     * Returns the set of entry IDs to unlock for the given item, or an empty
     * set if no mapping has been registered.
     */
    public static Set<ResourceLocation> entriesForItem(ResourceLocation itemId) {
        return ITEM_UNLOCKS.getOrDefault(itemId, Collections.emptySet());
    }

    /**
     * Returns the set of entry IDs to unlock for the given advancement, or an
     * empty set if no mapping has been registered.
     */
    public static Set<ResourceLocation> entriesForAdvancement(ResourceLocation advancementId) {
        return ADVANCEMENT_UNLOCKS.getOrDefault(advancementId, Collections.emptySet());
    }

    /**
     * Returns an unmodifiable view of the full item-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<ResourceLocation, Set<ResourceLocation>> getItemUnlocks() {
        return Collections.unmodifiableMap(ITEM_UNLOCKS);
    }

    /**
     * Returns an unmodifiable view of the full advancement-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<ResourceLocation, Set<ResourceLocation>> getAdvancementUnlocks() {
        return Collections.unmodifiableMap(ADVANCEMENT_UNLOCKS);
    }
}
