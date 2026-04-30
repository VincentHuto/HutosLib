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
    /** Catch-all for sources not covered by other constants. */
    OTHER
}
