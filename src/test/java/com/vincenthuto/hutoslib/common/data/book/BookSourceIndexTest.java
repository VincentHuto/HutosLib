package com.vincenthuto.hutoslib.common.data.book;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookSourceIndexTest {
    @Test
    void glossaryOwnershipAndForwardLinksUseTheFullCanonicalIdentity() {
        Map<ResourceLocation, BookDataTemplate> definitions = new LinkedHashMap<>();
        put(definitions, "test:guide/glossary/valid", new GlossaryTermTemplate(id("test:guide"), "Witness",
                List.of(id("other:guide/intro/pages/entry"))));
        put(definitions, "test:guide/glossary/wrong", new GlossaryTermTemplate(id("other:guide"), "Wrong", List.of()));
        for (String namespace : List.of("test", "other")) {
            put(definitions, namespace + ":guide/book", book());
            put(definitions, namespace + ":guide/intro/chapter", chapter(0));
            put(definitions, namespace + ":guide/intro/pages/entry", page(0));
        }
        BookPlaceboReloadListener listener = new BookPlaceboReloadListener();
        listener.bindBooks(definitions);
        BookCodeModel bound = listener.getBookByTitle(id("test:guide"));
        assertEquals(List.of("Witness"), bound.getGlossary().stream().map(GlossaryTermTemplate::getTerm).toList());
        assertEquals(id("other:guide"), listener.findTarget(id("other:guide/intro/pages/entry")).orElseThrow().bookId());
        assertTrue(listener.findTarget(id("other:guide/intro/entry")).isEmpty());
        assertTrue(listener.resolveBookId(null, "guide/").isEmpty(), "Ambiguous legacy prefixes cannot choose a namespace");
        assertEquals(id("other:guide"), listener.resolveBookId(id("other:guide"), "guide/").orElseThrow());
    }

    @Test
    void filteringDoesNotRenumberEntriesOrDropReaderCallbacks() {
        Map<ResourceLocation, BookDataTemplate> definitions = new LinkedHashMap<>();
        put(definitions, "test:guide/book", book());
        put(definitions, "test:guide/intro/chapter", chapter(0));
        put(definitions, "test:guide/intro/pages/a", page(0));
        put(definitions, "test:guide/intro/pages/b", page(1));
        BookPlaceboReloadListener listener = new BookPlaceboReloadListener();
        listener.bindBooks(definitions);
        BookCodeModel bound = listener.getBookByTitle(id("test:guide"));
        bound.setRedactionPredicate((player, level) -> level <= 3);
        var chapter = bound.getChapters().getFirst();
        var filtered = bound.copyWithChapters(List.of(chapter.copyWithPages(List.of(chapter.getPages().getLast()))));
        assertEquals(2, filtered.getSourceIndex().entry(id("test:guide/intro/pages/b")).orElseThrow().entryNumber());
        assertEquals(1, filtered.getSourceIndex().entry(id("test:guide/intro/pages/b")).orElseThrow().chapterNumber());
        assertTrue(filtered.canReveal(null, 3));
        assertFalse(filtered.canReveal(null, 4));
        assertSame(bound.getSourceIndex(), filtered.getSourceIndex());
        assertEquals(2, filtered.getSourceIndex().sourceChapters().getFirst().getPages().size(),
                "Pagination must retain undiscovered source entries");
    }

    @Test
    void contentVersionIgnoresAppearanceButDetectsRewritesAndRevealRules() {
        PageTemplate page = page(0);
        String original = BookSourceIndex.contentVersion(page);
        page.setTexture("test:reskin");
        page.setOrdinality(90);
        assertEquals(original, BookSourceIndex.contentVersion(page));
        page.setText("Rewritten");
        assertNotEquals(original, BookSourceIndex.contentVersion(page));
        String rewritten = BookSourceIndex.contentVersion(page);
        page.setRequiresEntry("test:gate");
        assertNotEquals(rewritten, BookSourceIndex.contentVersion(page));
        assertEquals(64, original.length());
    }

    private static ResourceLocation id(String id) { return ResourceLocation.parse(id); }
    private static void put(Map<ResourceLocation, BookDataTemplate> definitions, String id, BookDataTemplate value) {
        value.setId(id(id));
        definitions.put(value.getId(), value);
    }
    private static BookTemplate book() { return new BookTemplate("test:cover", "test:overlay", "Book", "", "", "minecraft:book"); }
    private static ChapterTemplate chapter(int order) { return new ChapterTemplate(order, "test:page", "1,0,0", "Chapter", "", "minecraft:book"); }
    private static PageTemplate page(int order) { return new PageTemplate(order, "test:page", "Entry", "", "Text", "minecraft:book"); }
}
