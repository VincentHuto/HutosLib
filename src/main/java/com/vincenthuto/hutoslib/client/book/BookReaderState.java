package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vincenthuto.hutoslib.common.book.ReaderLocation;
import net.minecraft.resources.ResourceLocation;
import java.util.*;

/** Persistent state for one player. It does not grant access to any content. */
public final class BookReaderState {
    public static final String UNKNOWN_VERSION = "unknown";
    public enum EntryStatus { NEW, UPDATED, UNREAD, READ }
    public record ReadStamp(String version, Set<Integer> levels) {
        public ReadStamp { levels = Set.copyOf(levels); }
    }
    public record RevealStamp(ResourceLocation entryId, String version, int level) {}

    private final Set<ResourceLocation> listed = new HashSet<>();
    private final Map<ResourceLocation, ReadStamp> read = new HashMap<>();
    private final Map<ResourceLocation, ReaderLocation> resumes = new HashMap<>();
    private final Map<ResourceLocation, ReaderLocation> bookmarks = new HashMap<>();
    private final Set<RevealStamp> reveals = new HashSet<>();
    private boolean dirty;

    public boolean isListed(ResourceLocation entry) { return listed.contains(entry); }
    public ReadStamp readStamp(ResourceLocation entry) { return read.get(entry); }
    public Map<ResourceLocation, ReaderLocation> bookmarks() { return Collections.unmodifiableMap(bookmarks); }
    public Optional<ReaderLocation> resume(ResourceLocation book) { return Optional.ofNullable(resumes.get(book)); }
    public boolean dirty() { return dirty; }
    public void saved() { dirty = false; }

    public EntryStatus status(ResourceLocation entry, String version, Set<Integer> levels) {
        ReadStamp stamp = read.get(entry);
        if (!listed.contains(entry) && stamp == null) return EntryStatus.NEW;
        if (stamp != null && !stamp.version().equals(UNKNOWN_VERSION) && !stamp.version().equals(version)) return EntryStatus.UPDATED;
        if (stamp == null || !stamp.levels().containsAll(levels)) return EntryStatus.UNREAD;
        return EntryStatus.READ;
    }

    public void markListed(ResourceLocation entry) { if (entry != null) dirty |= listed.add(entry); }

    public void markRead(ResourceLocation entry, String version, Set<Integer> levels) {
        if (entry == null) return;
        markListed(entry);
        ReadStamp stamp = new ReadStamp(version, levels);
        dirty |= !stamp.equals(read.put(entry, stamp));
    }

    public void unacknowledge(Collection<ResourceLocation> entries) {
        dirty |= listed.removeAll(entries);
        dirty |= read.keySet().removeAll(entries);
    }

    public void resume(ReaderLocation location) { dirty |= !location.equals(resumes.put(location.bookId(), location)); }

    public boolean toggleBookmark(ReaderLocation location) {
        dirty = true;
        if (bookmarks.remove(location.entryId()) != null) return false;
        bookmarks.put(location.entryId(), location);
        return true;
    }

    public boolean markRevealViewed(ResourceLocation entry, String version, int level) {
        if (level <= 0) return false;
        boolean added = reveals.add(new RevealStamp(entry, version, level));
        dirty |= added;
        return added;
    }

    public boolean hasViewedReveal(ResourceLocation entry, String version, int level) {
        return reveals.contains(new RevealStamp(entry, version, level));
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        JsonArray listedIds = new JsonArray();
        new TreeSet<>(listed).forEach(id -> listedIds.add(id.toString()));
        result.add("listed", listedIds);
        JsonObject readJson = new JsonObject();
        new TreeMap<>(read).forEach((id, stamp) -> {
            JsonObject value = new JsonObject();
            value.addProperty("version", stamp.version());
            JsonArray levels = new JsonArray();
            new TreeSet<>(stamp.levels()).forEach(levels::add);
            value.add("levels", levels);
            readJson.add(id.toString(), value);
        });
        result.add("read", readJson);
        JsonArray resumeJson = new JsonArray();
        new TreeMap<>(resumes).values().forEach(location -> resumeJson.add(location.toJson()));
        result.add("resume", resumeJson);
        JsonArray bookmarkJson = new JsonArray();
        new TreeMap<>(bookmarks).values().forEach(location -> bookmarkJson.add(location.toJson()));
        result.add("bookmarks", bookmarkJson);
        JsonArray revealJson = new JsonArray();
        reveals.stream().sorted(Comparator.comparing(RevealStamp::entryId).thenComparing(RevealStamp::version).thenComparingInt(RevealStamp::level))
                .forEach(stamp -> {
                    JsonObject value = new JsonObject();
                    value.addProperty("entry", stamp.entryId().toString());
                    value.addProperty("version", stamp.version());
                    value.addProperty("level", stamp.level());
                    revealJson.add(value);
                });
        result.add("reveals", revealJson);
        return result;
    }

    public static BookReaderState fromJson(JsonElement source) {
        BookReaderState state = new BookReaderState();
        if (source.isJsonArray()) {
            source.getAsJsonArray().forEach(value -> state.markRead(ResourceLocation.parse(value.getAsString()), UNKNOWN_VERSION, Set.of()));
            return state;
        }
        JsonObject object = source.getAsJsonObject();
        array(object, "listed").forEach(value -> state.listed.add(ResourceLocation.parse(value.getAsString())));
        if (object.has("read")) object.getAsJsonObject("read").entrySet().forEach(entry -> {
            JsonObject value = entry.getValue().getAsJsonObject();
            Set<Integer> levels = new HashSet<>();
            array(value, "levels").forEach(level -> levels.add(level.getAsInt()));
            state.read.put(ResourceLocation.parse(entry.getKey()), new ReadStamp(value.get("version").getAsString(), levels));
        });
        array(object, "resume").forEach(value -> {
            ReaderLocation location = ReaderLocation.fromJson(value.getAsJsonObject());
            state.resumes.put(location.bookId(), location);
        });
        array(object, "bookmarks").forEach(value -> {
            ReaderLocation location = ReaderLocation.fromJson(value.getAsJsonObject());
            state.bookmarks.put(location.entryId(), location);
        });
        array(object, "reveals").forEach(value -> {
            JsonObject reveal = value.getAsJsonObject();
            state.reveals.add(new RevealStamp(ResourceLocation.parse(reveal.get("entry").getAsString()),
                    reveal.get("version").getAsString(), reveal.get("level").getAsInt()));
        });
        return state;
    }

    private static JsonArray array(JsonObject object, String field) {
        return object.has(field) ? object.getAsJsonArray(field) : new JsonArray();
    }
}
