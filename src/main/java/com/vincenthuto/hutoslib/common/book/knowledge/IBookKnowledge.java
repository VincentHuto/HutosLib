package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

/**
 * Capability-style interface representing everything a player "knows" about a
 * book's entry/memo system. Mods attach a concrete implementation (e.g.
 * {@link BookKnowledge}) to the player via NeoForge's attachment system; each
 * mod registers its own {@code AttachmentType} key.
 *
 * <p>HutosLib declares the interface and default implementation only – it does
 * <em>not</em> register any attachment key itself.
 */
public interface IBookKnowledge {

    /**
     * Attempts to unlock {@code entryId}, recording {@code source} as the
     * discovery cause. Returns {@code true} if the entry was newly added.
     */
    boolean unlockEntry(ResourceLocation entryId, IDiscoverySource source);

    /**
     * Returns {@code true} if the player already has {@code entryId} unlocked.
     */
    boolean hasEntry(ResourceLocation entryId);

    /**
     * Returns an unmodifiable snapshot of all currently unlocked entry IDs.
     */
    Set<ResourceLocation> getUnlockedEntries();

    /**
     * Returns {@code true} if the player already knows {@code memoId}.
     */
    boolean knowsMemo(ResourceLocation memoId);

    /**
     * Records {@code memoId} as known without tying it to any book entry.
     * Returns {@code true} if the memo was newly recorded.
     */
    boolean recordMemo(ResourceLocation memoId);

    /**
     * Records {@code memoId} and simultaneously unlocks the associated
     * {@code entryId}. Returns {@code true} if either was newly added.
     */
    boolean unlockMemo(ResourceLocation memoId, ResourceLocation entryId);

    /**
     * Returns an unmodifiable snapshot of all known memo IDs.
     */
    Set<ResourceLocation> getKnownMemos();

    /**
     * Returns an unmodifiable map of entry ID → set of discovery sources that
     * caused the entry to be unlocked.
     */
    Map<ResourceLocation, Set<IDiscoverySource>> getEntrySources();

    /**
     * Replaces all data in this instance with a copy of {@code other}'s data.
     * Used during capability sync (client ← server).
     */
    void setFrom(IBookKnowledge other);
}
