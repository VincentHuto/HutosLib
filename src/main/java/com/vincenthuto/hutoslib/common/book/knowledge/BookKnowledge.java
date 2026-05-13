package com.vincenthuto.hutoslib.common.book.knowledge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import com.vincenthuto.hutoslib.common.util.INBTSerializable;

import java.util.*;

/**
 * Default NBT-serializable implementation of {@link IBookKnowledge}.
 *
 * <p>An {@code AttachmentType<BookKnowledge>} is registered by HutosLib as
 * {@link com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes#BOOK_KNOWLEDGE}.
 * Use {@link BookKnowledgeProvider#get(net.minecraft.world.entity.player.Player)} for
 * convenient access.
 *
 * <p>Discovery sources are persisted by their {@link IDiscoverySource#name()}
 * string. Subclasses should override {@link #lookupSource(String)} to re-hydrate
 * their specific source enum on deserialization; the base implementation silently
 * drops unknown source names rather than throwing.
 *
 * <p>Mods that need additional data (e.g. a mod-specific discovery enum) should
 * extend this class, override {@link #lookupSource} and {@link #newSourceSet},
 * and register their own {@code AttachmentType} in their own registry class.
 */
public class BookKnowledge implements IBookKnowledge, INBTSerializable<CompoundTag> {

    private static final String TAG_ENTRIES = "UnlockedEntries";
    private static final String TAG_MEMOS   = "KnownMemos";
    private static final String TAG_SOURCES = "EntrySources";
    private static final int TAG_STRING = 8;

    private final Set<Identifier> unlockedEntries = new LinkedHashSet<>();
    private final Set<Identifier> knownMemos      = new LinkedHashSet<>();
    private final Map<Identifier, Set<IDiscoverySource>> entrySources = new LinkedHashMap<>();

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
    public boolean unlockEntry(Identifier entryId, IDiscoverySource source) {
        if (entryId == null) {
            return false;
        }
        boolean entryChanged = unlockedEntries.add(entryId);
        boolean sourceChanged = false;
        if (source != null) {
            sourceChanged = entrySources.computeIfAbsent(entryId, id -> newSourceSet()).add(source);
        }
        return entryChanged || sourceChanged;
    }

    @Override
    public boolean hasEntry(Identifier entryId) {
        return unlockedEntries.contains(entryId);
    }

    @Override
    public Set<Identifier> getUnlockedEntries() {
        return Collections.unmodifiableSet(unlockedEntries);
    }

    @Override
    public boolean knowsMemo(Identifier memoId) {
        return knownMemos.contains(memoId);
    }

    @Override
    public boolean recordMemo(Identifier memoId) {
        return memoId != null && knownMemos.add(memoId);
    }

    @Override
    public boolean unlockMemo(Identifier memoId, Identifier entryId) {
        boolean changed = memoId != null && knownMemos.add(memoId);
        return unlockEntry(entryId, null) || changed;
    }

    @Override
    public Set<Identifier> getKnownMemos() {
        return Collections.unmodifiableSet(knownMemos);
    }

    @Override
    public Map<Identifier, Set<IDiscoverySource>> getEntrySources() {
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
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static ListTag writeResourceLocationSet(Set<Identifier> values) {
        ListTag list = new ListTag();
        for (Identifier value : values) {
            list.add(StringTag.valueOf(value.toString()));
        }
        return list;
    }

    private static void readResourceLocationSet(ListTag list, Set<Identifier> target) {
        for (int i = 0; i < list.size(); i++) {
            Identifier id = list.getString(i).map(Identifier::tryParse).orElse(null);
            if (id != null) {
                target.add(id);
            }
        }
    }
}
