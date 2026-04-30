package com.vincenthuto.hutoslib.client.book;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;

import net.minecraft.resources.ResourceLocation;

/**
 * Client-side, session-scoped tracker that records which book entries a player
 * has already "seen" (i.e. the book screen was opened while those entries were
 * unlocked). Nothing is persisted to disk or synced over the network.
 *
 * <p>Entries that were unlocked while the player was offline will therefore
 * always appear as "unread" during the first session in which they open the
 * book, which is the intended behavior.
 *
 * <p>This class is the generic replacement for Hemomancy's
 * {@code LiberReadTracker}; it works with any {@link IBookKnowledge}
 * implementation and does not depend on any mod-specific types.
 */
public final class BookReadTracker {

    private static final Map<UUID, Set<ResourceLocation>> ACKNOWLEDGED = new HashMap<>();

    private BookReadTracker() {
    }

    /**
     * Marks a single entry ID as acknowledged (read) for this player.
     * Call this when the player actually views a specific page.
     *
     * @param playerId the player's UUID
     * @param entryId  the entry ID to mark as read; ignored if {@code null}
     */
    public static void acknowledge(UUID playerId, ResourceLocation entryId) {
        if (entryId != null) {
            ACKNOWLEDGED.computeIfAbsent(playerId, id -> new HashSet<>()).add(entryId);
        }
    }

    /**
     * Marks a collection of entry IDs as acknowledged (read) for this player.
     * Call this when the player opens a book screen.
     *
     * @param playerId the player's UUID
     * @param entryIds the set of entry IDs to mark as read
     */
    public static void acknowledge(UUID playerId, Collection<ResourceLocation> entryIds) {
        ACKNOWLEDGED.computeIfAbsent(playerId, id -> new HashSet<>()).addAll(entryIds);
    }

    /** Returns whether {@code entryId} has been acknowledged for this player. */
    public static boolean isAcknowledged(UUID playerId, ResourceLocation entryId) {
        return entryId != null
                && ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet()).contains(entryId);
    }

    /**
     * Counts how many explicit entry/page IDs are still unacknowledged.
     */
    public static int countUnread(UUID playerId, Collection<ResourceLocation> entryIds) {
        Set<ResourceLocation> seen = ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet());
        int count = 0;
        for (ResourceLocation entryId : entryIds) {
            if (entryId != null && !seen.contains(entryId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns {@code true} if the player has at least one unlocked entry whose
     * path starts with {@code bookPrefix} that has not yet been acknowledged.
     *
     * @param playerId   the player's UUID
     * @param knowledge  the player's knowledge capability
     * @param bookPrefix path prefix used to scope entries to a single book,
     *                   e.g. {@code "sanctumsanguinium/"} or
     *                   {@code "liberimmaculatus/"}
     */
    public static boolean hasUnread(UUID playerId, IBookKnowledge knowledge, String bookPrefix) {
        return countUnread(playerId, knowledge, bookPrefix) > 0;
    }

    /**
     * Returns the number of unlocked entries whose path starts with
     * {@code bookPrefix} that have not yet been acknowledged by this player.
     *
     * @param playerId   the player's UUID
     * @param knowledge  the player's knowledge capability
     * @param bookPrefix path prefix to scope the count to a single book
     */
    public static int countUnread(UUID playerId, IBookKnowledge knowledge, String bookPrefix) {
        Set<ResourceLocation> seen = ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet());
        int count = 0;
        for (ResourceLocation entry : knowledge.getUnlockedEntries()) {
            if (entry.getPath().startsWith(bookPrefix) && !seen.contains(entry)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Removes all tracked data for this player. Call on logout so stale data
     * does not bleed into the next session.
     *
     * @param playerId the player's UUID
     */
    public static void clear(UUID playerId) {
        ACKNOWLEDGED.remove(playerId);
    }
}
