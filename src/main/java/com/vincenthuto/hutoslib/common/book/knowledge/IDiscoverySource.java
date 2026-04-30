package com.vincenthuto.hutoslib.common.book.knowledge;

/**
 * Marker interface for discovery source types used when unlocking book entries.
 * Each mod declares its own enum (or other implementation) that implements this
 * interface. HutosLib provides {@link CommonDiscoverySource} for the most
 * universal cases; mods may add their own values freely.
 *
 * <p>Because this interface is typically implemented by enums, callers can rely
 * on {@link #name()} returning a stable, serialization-safe string identifier.
 */
public interface IDiscoverySource {
    /**
     * Returns a stable identifier for this source (enum constant name when the
     * implementing type is an enum).
     */
    String name();
}
