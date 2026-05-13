package com.vincenthuto.hutoslib.client.book;

import com.google.gson.*;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Client-side tracker that records which book pages/entries a player has read.
 * Data is persisted to a small JSON file under the game config directory so
 * read state survives reconnects and full client restarts.
 *
 * <p>This class is the generic replacement for Hemomancy's
 * {@code LiberReadTracker}; it works with any {@link IBookKnowledge}
 * implementation and does not depend on any mod-specific types.
 */
public final class BookReadTracker {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String ROOT_PLAYERS = "players";

    private static final Map<UUID, Set<Identifier>> ACKNOWLEDGED = new HashMap<>();
    private static boolean loaded;
    private static boolean dirty;

    private BookReadTracker() {
    }

    /**
     * Marks a single entry ID as acknowledged (read) for this player.
     * Call this when the player actually views a specific page.
     *
     * @param playerId the player's UUID
     * @param entryId  the entry ID to mark as read; ignored if {@code null}
     */
    public static void acknowledge(UUID playerId, Identifier entryId) {
        ensureLoaded();
        if (entryId != null) {
            boolean changed = ACKNOWLEDGED.computeIfAbsent(playerId, id -> new HashSet<>()).add(entryId);
            if (changed) {
                dirty = true;
                saveIfDirty();
            }
        }
    }

    /**
     * Marks a collection of entry IDs as acknowledged (read) for this player.
     * Call this when the player opens a book screen.
     *
     * @param playerId the player's UUID
     * @param entryIds the set of entry IDs to mark as read
     */
    public static void acknowledge(UUID playerId, Collection<Identifier> entryIds) {
        ensureLoaded();
        boolean changed = ACKNOWLEDGED.computeIfAbsent(playerId, id -> new HashSet<>()).addAll(entryIds);
        if (changed) {
            dirty = true;
            saveIfDirty();
        }
    }

    /**
     * Marks a collection of entry IDs as unread for this player. This is used
     * when a server sync reports entries that were just discovered, so stale
     * client-side read state from earlier accidental visibility cannot suppress
     * the "new entry" indicators.
     *
     * @param playerId the player's UUID
     * @param entryIds the entry IDs to remove from the acknowledged/read set
     */
    public static void unacknowledge(UUID playerId, Collection<Identifier> entryIds) {
        ensureLoaded();
        if (entryIds == null || entryIds.isEmpty()) {
            return;
        }
        Set<Identifier> acknowledged = ACKNOWLEDGED.get(playerId);
        if (acknowledged == null || acknowledged.isEmpty()) {
            return;
        }
        boolean changed = acknowledged.removeAll(entryIds);
        if (changed) {
            dirty = true;
            saveIfDirty();
        }
    }

    /** Returns whether {@code entryId} has been acknowledged for this player. */
    public static boolean isAcknowledged(UUID playerId, Identifier entryId) {
        ensureLoaded();
        return entryId != null
                && ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet()).contains(entryId);
    }

    /**
     * Counts how many explicit entry/page IDs are still unacknowledged.
     */
    public static int countUnread(UUID playerId, Collection<Identifier> entryIds) {
        ensureLoaded();
        Set<Identifier> seen = ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet());
        int count = 0;
        for (Identifier entryId : entryIds) {
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
        ensureLoaded();
        Set<Identifier> seen = ACKNOWLEDGED.getOrDefault(playerId, Collections.emptySet());
        int count = 0;
        for (Identifier entry : knowledge.getUnlockedEntries()) {
            if (entry.getPath().startsWith(bookPrefix) && !seen.contains(entry)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Permanently removes all tracked read data for this player from memory and
     * persisted storage.
     *
     * @param playerId the player's UUID
     */
    public static void clear(UUID playerId) {
        ensureLoaded();
        if (ACKNOWLEDGED.remove(playerId) != null) {
            dirty = true;
            saveIfDirty();
        }
    }

    /** Flushes any pending persisted tracker changes to disk. */
    public static void flush() {
        saveIfDirty();
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;

        Path path = storagePath();
        if (!Files.exists(path)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            if (!rootElement.isJsonObject()) {
                return;
            }
            JsonObject root = rootElement.getAsJsonObject();
            JsonObject players = root.has(ROOT_PLAYERS) && root.get(ROOT_PLAYERS).isJsonObject()
                    ? root.getAsJsonObject(ROOT_PLAYERS)
                    : null;
            if (players == null) {
                return;
            }

            for (Map.Entry<String, JsonElement> entry : players.entrySet()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(entry.getKey());
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
                if (!entry.getValue().isJsonArray()) {
                    continue;
                }

                Set<Identifier> ids = new HashSet<>();
                JsonArray array = entry.getValue().getAsJsonArray();
                for (JsonElement idElement : array) {
                    if (!idElement.isJsonPrimitive()) {
                        continue;
                    }
                    Identifier id = Identifier.tryParse(idElement.getAsString());
                    if (id != null) {
                        ids.add(id);
                    }
                }
                if (!ids.isEmpty()) {
                    ACKNOWLEDGED.put(uuid, ids);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void saveIfDirty() {
        if (!dirty) {
            return;
        }

        Path path = storagePath();
        try {
            Files.createDirectories(path.getParent());

            JsonObject root = new JsonObject();
            JsonObject players = new JsonObject();
            for (Map.Entry<UUID, Set<Identifier>> entry : ACKNOWLEDGED.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                JsonArray ids = new JsonArray();
                for (Identifier id : entry.getValue()) {
                    ids.add(id.toString());
                }
                players.add(entry.getKey().toString(), ids);
            }
            root.add(ROOT_PLAYERS, players);

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            dirty = false;
        } catch (IOException ignored) {
        }
    }

    private static Path storagePath() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config")
                .resolve("hutoslib_read_pages.json");
    }
}
