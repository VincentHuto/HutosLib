package com.vincenthuto.hutoslib.common.book.knowledge;

import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry that maps item, advancement, entity-type, biome, and
 * structure IDs to the book-entry {@link Identifier}s that should be
 * unlocked when the corresponding in-world event occurs.
 *
 * <p>Mods populate this registry during {@code FMLCommonSetupEvent} (or any
 * time before the first gameplay event fires):
 * <pre>{@code
 * BookEntryRegistry.registerItemUnlock(
 *         Identifier.fromNamespaceAndPath("mymod", "my_item"),
 *         Identifier.fromNamespaceAndPath("mymod", "guide/chapter"));
 *
 * BookEntryRegistry.registerAdvancementUnlock(
 *         Identifier.fromNamespaceAndPath("mymod", "advancements/root"),
 *         Identifier.fromNamespaceAndPath("mymod", "guide/chapter"));
 *
 * BookEntryRegistry.registerEntityKillUnlock(
 *         Identifier.fromNamespaceAndPath("minecraft", "zombie"),
 *         Identifier.fromNamespaceAndPath("mymod", "guide/undead"));
 *
 * BookEntryRegistry.registerBiomeUnlock(
 *         Identifier.fromNamespaceAndPath("minecraft", "deep_dark"),
 *         Identifier.fromNamespaceAndPath("mymod", "guide/darkness"));
 *
 * BookEntryRegistry.registerStructureUnlock(
 *         Identifier.fromNamespaceAndPath("minecraft", "stronghold"),
 *         Identifier.fromNamespaceAndPath("mymod", "guide/stronghold"));
 * }</pre>
 *
 * <p>HutosLib's {@link BookDiscoveryEvents} listens to the relevant game-bus
 * events and consults this registry automatically, so mods only need to
 * register their mappings here — no additional event listener is required.
 */
public final class BookEntryRegistry {

    /** item registry ID → set of entry IDs to unlock on pickup */
    private static final Map<Identifier, Set<Identifier>> ITEM_UNLOCKS =
            new ConcurrentHashMap<>();

    /** advancement ID → set of entry IDs to unlock when earned */
    private static final Map<Identifier, Set<Identifier>> ADVANCEMENT_UNLOCKS =
            new ConcurrentHashMap<>();

    /** entity-type registry ID → set of entry IDs to unlock on kill */
    private static final Map<Identifier, Set<Identifier>> ENTITY_KILL_UNLOCKS =
            new ConcurrentHashMap<>();

    /** biome registry ID → set of entry IDs to unlock on first entry */
    private static final Map<Identifier, Set<Identifier>> BIOME_UNLOCKS =
            new ConcurrentHashMap<>();

    /** structure registry ID → set of entry IDs to unlock on first discovery */
    private static final Map<Identifier, Set<Identifier>> STRUCTURE_UNLOCKS =
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
     * @throws NullPointerException if {@code itemId} or {@code entryId} is null
     */
    public static void registerItemUnlock(Identifier itemId, Identifier entryId) {
        Objects.requireNonNull(itemId,  "itemId must not be null");
        Objects.requireNonNull(entryId, "entryId must not be null");
        ITEM_UNLOCKS.computeIfAbsent(itemId, k -> ConcurrentHashMap.newKeySet()).add(entryId);
    }

    /**
     * Registers that earning the advancement identified by {@code advancementId}
     * should unlock the book entry {@code entryId}.
     *
     * @param advancementId the advancement's resource location
     * @param entryId       the book entry to unlock
     * @throws NullPointerException if {@code advancementId} or {@code entryId} is null
     */
    public static void registerAdvancementUnlock(Identifier advancementId,
            Identifier entryId) {
        Objects.requireNonNull(advancementId, "advancementId must not be null");
        Objects.requireNonNull(entryId,       "entryId must not be null");
        ADVANCEMENT_UNLOCKS.computeIfAbsent(advancementId, k -> ConcurrentHashMap.newKeySet())
                .add(entryId);
    }

    /**
     * Registers that killing the entity type identified by {@code entityTypeId}
     * should unlock the book entry {@code entryId}.
     *
     * @param entityTypeId registry key of the entity type (e.g. {@code minecraft:zombie})
     * @param entryId      the book entry to unlock
     * @throws NullPointerException if {@code entityTypeId} or {@code entryId} is null
     */
    public static void registerEntityKillUnlock(Identifier entityTypeId,
            Identifier entryId) {
        Objects.requireNonNull(entityTypeId, "entityTypeId must not be null");
        Objects.requireNonNull(entryId,      "entryId must not be null");
        ENTITY_KILL_UNLOCKS.computeIfAbsent(entityTypeId, k -> ConcurrentHashMap.newKeySet())
                .add(entryId);
    }

    /**
     * Registers that entering the biome identified by {@code biomeId} should
     * unlock the book entry {@code entryId}.
     *
     * @param biomeId the biome's resource location (e.g. {@code minecraft:deep_dark})
     * @param entryId the book entry to unlock
     * @throws NullPointerException if {@code biomeId} or {@code entryId} is null
     */
    public static void registerBiomeUnlock(Identifier biomeId, Identifier entryId) {
        Objects.requireNonNull(biomeId,  "biomeId must not be null");
        Objects.requireNonNull(entryId, "entryId must not be null");
        BIOME_UNLOCKS.computeIfAbsent(biomeId, k -> ConcurrentHashMap.newKeySet()).add(entryId);
    }

    /**
     * Registers that entering (discovering) the structure identified by
     * {@code structureId} should unlock the book entry {@code entryId}.
     *
     * @param structureId the structure's resource location (e.g. {@code minecraft:stronghold})
     * @param entryId     the book entry to unlock
     * @throws NullPointerException if {@code structureId} or {@code entryId} is null
     */
    public static void registerStructureUnlock(Identifier structureId,
            Identifier entryId) {
        Objects.requireNonNull(structureId, "structureId must not be null");
        Objects.requireNonNull(entryId,     "entryId must not be null");
        STRUCTURE_UNLOCKS.computeIfAbsent(structureId, k -> ConcurrentHashMap.newKeySet())
                .add(entryId);
    }

    // -------------------------------------------------------------------------
    // Lookup
    // -------------------------------------------------------------------------

    /**
     * Returns the set of entry IDs to unlock for the given item, or an empty
     * set if no mapping has been registered.
     */
    public static Set<Identifier> entriesForItem(Identifier itemId) {
        return ITEM_UNLOCKS.getOrDefault(itemId, Collections.emptySet());
    }

    /**
     * Returns the set of entry IDs to unlock for the given advancement, or an
     * empty set if no mapping has been registered.
     */
    public static Set<Identifier> entriesForAdvancement(Identifier advancementId) {
        return ADVANCEMENT_UNLOCKS.getOrDefault(advancementId, Collections.emptySet());
    }

    /**
     * Returns the set of entry IDs to unlock for killing the given entity type,
     * or an empty set if no mapping has been registered.
     */
    public static Set<Identifier> entriesForEntityKill(Identifier entityTypeId) {
        return ENTITY_KILL_UNLOCKS.getOrDefault(entityTypeId, Collections.emptySet());
    }

    /**
     * Returns the set of entry IDs to unlock for entering the given biome,
     * or an empty set if no mapping has been registered.
     */
    public static Set<Identifier> entriesForBiome(Identifier biomeId) {
        return BIOME_UNLOCKS.getOrDefault(biomeId, Collections.emptySet());
    }

    /**
     * Returns the set of entry IDs to unlock for entering the given structure,
     * or an empty set if no mapping has been registered.
     */
    public static Set<Identifier> entriesForStructure(Identifier structureId) {
        return STRUCTURE_UNLOCKS.getOrDefault(structureId, Collections.emptySet());
    }

    /**
     * Returns an unmodifiable view of the full item-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<Identifier, Set<Identifier>> getItemUnlocks() {
        return Collections.unmodifiableMap(ITEM_UNLOCKS);
    }

    /**
     * Returns an unmodifiable view of the full advancement-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<Identifier, Set<Identifier>> getAdvancementUnlocks() {
        return Collections.unmodifiableMap(ADVANCEMENT_UNLOCKS);
    }

    /**
     * Returns an unmodifiable view of the full entity-kill-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<Identifier, Set<Identifier>> getEntityKillUnlocks() {
        return Collections.unmodifiableMap(ENTITY_KILL_UNLOCKS);
    }

    /**
     * Returns an unmodifiable view of the full biome-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<Identifier, Set<Identifier>> getBiomeUnlocks() {
        return Collections.unmodifiableMap(BIOME_UNLOCKS);
    }

    /**
     * Returns an unmodifiable view of the full structure-to-entries map.
     * Useful for inspection or debugging.
     */
    public static Map<Identifier, Set<Identifier>> getStructureUnlocks() {
        return Collections.unmodifiableMap(STRUCTURE_UNLOCKS);
    }
}
