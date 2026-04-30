package com.vincenthuto.hutoslib.common.book.knowledge;

/**
 * Generic discovery sources available to any HutosLib-based book system.
 * Mods that need additional sources should declare their own enum that
 * implements {@link IDiscoverySource}; they can freely include these common
 * values or ignore them as needed.
 */
public enum CommonDiscoverySource implements IDiscoverySource {
    /** Entry was unlocked by completing an advancement. */
    ADVANCEMENT,
    /** Entry was unlocked by picking up a specific item. */
    ITEM_PICKUP,
    /** Entry was unlocked by killing a specific entity type. */
    ENTITY_KILL,
    /** Entry was unlocked by entering a specific biome. */
    BIOME_ENTER,
    /** Entry was unlocked by entering or discovering a specific structure. */
    STRUCTURE_DISCOVER,
    /** Catch-all for sources not covered by other constants. */
    OTHER
}
