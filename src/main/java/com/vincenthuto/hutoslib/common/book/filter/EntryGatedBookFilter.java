package com.vincenthuto.hutoslib.common.book.filter;

import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic {@link IBookPageFilter} that hides pages whose
 * {@link PageTemplate#getRequiresEntry() requiresEntry} field references an
 * entry the player has not yet unlocked in their {@link IBookKnowledge}.
 *
 * <p>Chapters whose every page is locked are omitted from the result entirely.
 * Chapters that contain no {@code requiresEntry} pages (or whose locked pages
 * are unlocked) are included unchanged.
 *
 * <p>Use {@link #INSTANCE} as a stateless singleton — the filter inspects
 * player state at call time so it is safe to share.
 */
public final class EntryGatedBookFilter implements IBookPageFilter {

    /** Shared singleton; safe for concurrent use. */
    public static final EntryGatedBookFilter INSTANCE = new EntryGatedBookFilter();

    private EntryGatedBookFilter() {
    }

    @Override
    public BookCodeModel filter(BookCodeModel source, Player player) {
        return filter(source, BookKnowledgeProvider.get(player));
    }

    /**
     * Variant used by mods whose book UI reads knowledge from a custom
     * attachment instead of HutosLib's default {@link BookKnowledgeProvider}.
     */
    public BookCodeModel filter(BookCodeModel source, @Nullable IBookKnowledge knowledge) {
        if (source == null) {
            return null;
        }

        List<ChapterTemplate> filteredChapters = new ArrayList<>();

        for (ChapterTemplate chapter : source.getChapters()) {
            List<BookDataTemplate> visiblePages = new ArrayList<>();

            for (BookDataTemplate page : chapter.getPages()) {
                if (page instanceof PageTemplate pt && !pt.getRequiresEntry().isEmpty()) {
                    ResourceLocation entryId = ResourceLocation.tryParse(pt.getRequiresEntry());
                    if (entryId == null || knowledge == null || !knowledge.hasEntry(entryId)) {
                        continue; // locked or malformed entry ID — skip this page
                    }
                }
                visiblePages.add(page);
            }

            if (visiblePages.isEmpty()) {
                continue; // all pages locked — hide the whole chapter tab
            }

            ChapterTemplate copy = new ChapterTemplate(
                    chapter.getOrdinality(),
                    chapter.getTexture(),
                    chapter.getColor(),
                    chapter.getTitle(),
                    chapter.getSubtitle(),
                    chapter.getIcon());
            if (chapter.getId() != null) {
                copy.setId(chapter.getId());
            }
            copy.setPages(visiblePages);
            filteredChapters.add(copy);
        }

        BookCodeModel filtered = new BookCodeModel(source.getResourceLocation(), source.getTemplate());
        filtered.setChapters(filteredChapters);
        filtered.setTheme(source.getTheme());
        return filtered;
    }
}
