package com.vincenthuto.hutoslib.common.book.knowledge;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Default NBT-serializable implementation of {@link IBookKnowledge}.
 *
 * <p>Discovery sources are persisted by their {@link IDiscoverySource#name()}
 * string. Subclasses should override {@link #lookupSource(String)} to re-hydrate
 * their specific source enum on deserialization; the base implementation silently
 * drops unknown source names rather than throwing.
 *
 * <p>The attachment key itself is intentionally <em>not</em> declared here –
 * each mod registers its own {@code AttachmentType} in its own registry class.
 */
public class BookKnowledge implements IBookKnowledge, INBTSerializable<CompoundTag> {

    private static final String TAG_ENTRIES = "UnlockedEntries";
    private static final String TAG_MEMOS   = "KnownMemos";
    private static final String TAG_SOURCES = "EntrySources";
    private static final int TAG_STRING = 8;

    private final Set<ResourceLocation> unlockedEntries = new LinkedHashSet<>();
    private final Set<ResourceLocation> knownMemos      = new LinkedHashSet<>();
    private final Map<ResourceLocation, Set<IDiscoverySource>> entrySources = new LinkedHashMap<>();

    // -------------------------------------------------------------------------
    // Overrideable helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a new, mutable source set for an entry. Subclasses may return an
     * {@code EnumSet} when they use a concrete enum type.
     */
    protected Set<IDiscoverySource> newSourceSet() {
        return new HashSet<>();
    }

    /**
     * Converts a persisted source name back to an {@link IDiscoverySource}.
     * The default implementation returns {@link Optional#empty()} for every
     * name, effectively skipping source data on deserialization.
     *
     * <p>Subclasses that use a specific enum override this method, e.g.:
     * <pre>{@code
     * protected Optional<IDiscoverySource> lookupSource(String name) {
     *     try { return Optional.of(MyDiscoverySource.valueOf(name)); }
     *     catch (IllegalArgumentException e) { return Optional.empty(); }
     * }
     * }</pre>
     */
    protected Optional<IDiscoverySource> lookupSource(String name) {
        return Optional.empty();
    }

    // -------------------------------------------------------------------------
    // IBookKnowledge implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean unlockEntry(ResourceLocation entryId, IDiscoverySource source) {
        if (entryId == null) {
            return false;
        }
        boolean changed = unlockedEntries.add(entryId);
        if (source != null) {
            entrySources.computeIfAbsent(entryId, id -> newSourceSet()).add(source);
        }
        return changed;
    }

    @Override
    public boolean hasEntry(ResourceLocation entryId) {
        return unlockedEntries.contains(entryId);
    }

    @Override
    public Set<ResourceLocation> getUnlockedEntries() {
        return Collections.unmodifiableSet(unlockedEntries);
    }

    @Override
    public boolean knowsMemo(ResourceLocation memoId) {
        return knownMemos.contains(memoId);
    }

    @Override
    public boolean recordMemo(ResourceLocation memoId) {
        return memoId != null && knownMemos.add(memoId);
    }

    @Override
    public boolean unlockMemo(ResourceLocation memoId, ResourceLocation entryId) {
        boolean changed = memoId != null && knownMemos.add(memoId);
        return unlockEntry(entryId, null) || changed;
    }

    @Override
    public Set<ResourceLocation> getKnownMemos() {
        return Collections.unmodifiableSet(knownMemos);
    }

    @Override
    public Map<ResourceLocation, Set<IDiscoverySource>> getEntrySources() {
        return Collections.unmodifiableMap(entrySources);
    }

    @Override
    public void setFrom(IBookKnowledge other) {
        unlockedEntries.clear();
        knownMemos.clear();
        entrySources.clear();
        unlockedEntries.addAll(other.getUnlockedEntries());
        knownMemos.addAll(other.getKnownMemos());
        other.getEntrySources().forEach((entry, sources) -> {
            Set<IDiscoverySource> copy = newSourceSet();
            copy.addAll(sources);
            entrySources.put(entry, copy);
        });
    }

    // -------------------------------------------------------------------------
    // NBT serialization
    // -------------------------------------------------------------------------

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put(TAG_ENTRIES, writeResourceLocationSet(unlockedEntries));
        tag.put(TAG_MEMOS,   writeResourceLocationSet(knownMemos));

        CompoundTag sourcesTag = new CompoundTag();
        entrySources.forEach((entry, sources) -> {
            ListTag sourceList = new ListTag();
            for (IDiscoverySource source : sources) {
                sourceList.add(StringTag.valueOf(source.name()));
            }
            sourcesTag.put(entry.toString(), sourceList);
        });
        tag.put(TAG_SOURCES, sourcesTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        unlockedEntries.clear();
        knownMemos.clear();
        entrySources.clear();

        readResourceLocationSet(tag.getList(TAG_ENTRIES, TAG_STRING), unlockedEntries);
        readResourceLocationSet(tag.getList(TAG_MEMOS,   TAG_STRING), knownMemos);

        CompoundTag sourcesTag = tag.getCompound(TAG_SOURCES);
        for (String entryKey : sourcesTag.getAllKeys()) {
            ResourceLocation entryId = ResourceLocation.tryParse(entryKey);
            if (entryId == null) {
                continue;
            }
            Set<IDiscoverySource> sources = newSourceSet();
            ListTag sourceList = sourcesTag.getList(entryKey, TAG_STRING);
            for (int i = 0; i < sourceList.size(); i++) {
                lookupSource(sourceList.getString(i)).ifPresent(sources::add);
            }
            entrySources.put(entryId, sources);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static ListTag writeResourceLocationSet(Set<ResourceLocation> values) {
        ListTag list = new ListTag();
        for (ResourceLocation value : values) {
            list.add(StringTag.valueOf(value.toString()));
        }
        return list;
    }

    private static void readResourceLocationSet(ListTag list, Set<ResourceLocation> target) {
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null) {
                target.add(id);
            }
        }
    }
}
