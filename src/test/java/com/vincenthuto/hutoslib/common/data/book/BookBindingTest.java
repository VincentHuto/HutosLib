package com.vincenthuto.hutoslib.common.data.book;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookBindingTest {
    @Test
    void sameBookAndChapterPathsInDifferentModsNeverMix() {
        Map<ResourceLocation, BookDataTemplate> definitions = new LinkedHashMap<>();
        for (String namespace : List.of("first", "second")) {
            put(definitions, namespace + ":guide/book", book());
            put(definitions, namespace + ":guide/intro/chapter", chapter(0));
            put(definitions, namespace + ":guide/intro/pages/entry", page(0));
        }
        BookPlaceboReloadListener listener = new BookPlaceboReloadListener();
        listener.bindBooks(definitions);

        BookCodeModel first = listener.getBookByTitle(ResourceLocation.parse("first:guide"));
        assertEquals(1, first.getChapters().size());
        assertEquals(List.of(ResourceLocation.parse("first:guide/intro/pages/entry")),
                first.getChapters().getFirst().getPages().stream().map(BookDataTemplate::getId).toList());
        assertEquals(1, listener.getBookByTitle(ResourceLocation.parse("second:guide")).getTotalPages());
    }

    @Test
    void bindingSortsChaptersAndTiedPagesWithoutOpeningAScreen() {
        Map<ResourceLocation, BookDataTemplate> definitions = new LinkedHashMap<>();
        put(definitions, "test:guide/book", book());
        put(definitions, "test:guide/late/chapter", chapter(9));
        put(definitions, "test:guide/early/chapter", chapter(1));
        put(definitions, "test:guide/early/pages/z", page(0));
        put(definitions, "test:guide/early/pages/a", page(0));
        BookPlaceboReloadListener listener = new BookPlaceboReloadListener();
        listener.bindBooks(definitions);

        BookCodeModel bound = listener.getBookByTitle(ResourceLocation.parse("test:guide"));
        assertEquals(List.of(1, 9), bound.getChapters().stream().map(ChapterTemplate::getOrdinality).toList());
        assertEquals(List.of(ResourceLocation.parse("test:guide/early/pages/a"), ResourceLocation.parse("test:guide/early/pages/z")),
                bound.getChapters().getFirst().getPages().stream().map(BookDataTemplate::getId).toList());
    }

    private static void put(Map<ResourceLocation, BookDataTemplate> definitions, String id, BookDataTemplate value) {
        value.setId(ResourceLocation.parse(id));
        definitions.put(value.getId(), value);
    }

    private static BookTemplate book() {
        return new BookTemplate("test:cover", "test:overlay", "Book", "", "", "minecraft:book");
    }

    private static ChapterTemplate chapter(int order) {
        return new ChapterTemplate(order, "test:page", "1,0,0", "Chapter", "", "minecraft:book");
    }

    private static PageTemplate page(int order) {
        return new PageTemplate(order, "test:page", "Entry", "", "Text", "minecraft:book");
    }
}
