package com.vincenthuto.hutoslib.client.book;

import com.google.gson.*;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Versioned client storage. Existing acknowledgement APIs remain compatibility wrappers. */
public final class BookReadTracker {
    private static final Logger LOGGER = LogManager.getLogger(BookReadTracker.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int FORMAT_VERSION = 2;
    private static final Map<UUID, BookReaderState> PLAYERS = new HashMap<>();
    private static boolean loaded;
    private static boolean dirty;
    private static boolean writable = true;

    private BookReadTracker() {}

    public static BookReaderState state(UUID playerId) {
        ensureLoaded();
        return PLAYERS.computeIfAbsent(Objects.requireNonNull(playerId), ignored -> new BookReaderState());
    }

    public static void acknowledge(UUID playerId, ResourceLocation entryId) {
        if (entryId != null && state(playerId).readStamp(entryId) == null)
            state(playerId).markRead(entryId, BookReaderState.UNKNOWN_VERSION, Set.of());
    }

    public static void acknowledge(UUID playerId, Collection<ResourceLocation> entryIds) {
        if (entryIds != null) entryIds.forEach(id -> acknowledge(playerId, id));
    }

    public static void unacknowledge(UUID playerId, Collection<ResourceLocation> entryIds) {
        if (entryIds != null) state(playerId).unacknowledge(entryIds);
    }

    public static boolean isAcknowledged(UUID playerId, ResourceLocation entryId) {
        return entryId != null && state(playerId).readStamp(entryId) != null;
    }

    public static int countUnread(UUID playerId, Collection<ResourceLocation> entryIds) {
        BookReaderState state = state(playerId);
        return (int) entryIds.stream().filter(Objects::nonNull).distinct().filter(id -> state.readStamp(id) == null).count();
    }

    public static boolean hasUnread(UUID playerId, IBookKnowledge knowledge, String bookPrefix) {
        return countUnread(playerId, knowledge, bookPrefix) > 0;
    }

    public static int countUnread(UUID playerId, IBookKnowledge knowledge, String bookPrefix) {
        if (knowledge == null || bookPrefix == null) return 0;
        return countUnread(playerId, knowledge.getUnlockedEntries().stream().filter(id -> id.getPath().startsWith(bookPrefix)).toList());
    }

    public static void clear(UUID playerId) {
        ensureLoaded();
        dirty |= PLAYERS.remove(playerId) != null;
    }

    public static void flush() {
        if (!loaded || !writable || (!dirty && PLAYERS.values().stream().noneMatch(BookReaderState::dirty))) return;
        Path path = storagePath();
        try {
            Files.createDirectories(path.getParent());
            JsonObject root = new JsonObject(), players = new JsonObject();
            root.addProperty("version", FORMAT_VERSION);
            new TreeMap<>(PLAYERS).forEach((id, state) -> players.add(id.toString(), state.toJson()));
            root.add("players", players);
            Path temporary = Files.createTempFile(path.getParent(), "hutoslib-read-pages-", ".tmp");
            try {
                Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
                try { Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
                catch (AtomicMoveNotSupportedException ignored) { Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING); }
            } finally { Files.deleteIfExists(temporary); }
            dirty = false;
            PLAYERS.values().forEach(BookReaderState::saved);
        } catch (IOException exception) {
            LOGGER.warn("Cannot save book reader state to {}", path, exception);
        }
    }

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path path = storagePath();
        if (!Files.exists(path)) return;
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            int version = root.has("version") ? root.get("version").getAsInt() : 1;
            if (version > FORMAT_VERSION) throw new IOException("Reader state uses unsupported format " + version);
            Map<UUID, BookReaderState> parsed = new HashMap<>();
            if (root.has("players")) root.getAsJsonObject("players").entrySet().forEach(entry ->
                    parsed.put(UUID.fromString(entry.getKey()), BookReaderState.fromJson(entry.getValue())));
            if (version < FORMAT_VERSION) {
                Path backup = path.resolveSibling(path.getFileName() + ".v1.bak");
                if (!Files.exists(backup)) Files.copy(path, backup);
                dirty = true;
            }
            PLAYERS.putAll(parsed);
        } catch (IOException | RuntimeException exception) {
            writable = false;
            LOGGER.warn("Cannot read book state at {}; preserving the existing file without overwriting it", path, exception);
        }
    }

    private static Path storagePath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("hutoslib_read_pages.json");
    }
}
