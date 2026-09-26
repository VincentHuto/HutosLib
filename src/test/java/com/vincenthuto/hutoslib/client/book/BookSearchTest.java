package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

class BookSearchTest {
    @Test
    void concealedMatchesNeverReturnHiddenTitlesOrSnippets() {
        var hidden = content("hidden", "Hidden name", "A secret signal", 3);
        var results = BookSearch.query(List.of(hidden), "signal", level -> false);
        assertEquals(1, results.size());
        assertTrue(results.getFirst().concealed());
        assertEquals("", results.getFirst().title());
        assertEquals("", results.getFirst().snippet());
        assertEquals(3, results.getFirst().requirement());
    }

    @Test
    void inlineHiddenOnlyMatchesHaveAPublicTitleButNoFragment() {
        var partial = content("partial", "Public title", "Visible {wash:2|secret signal}", 0);
        var hiddenMatch = BookSearch.query(List.of(partial), "signal", level -> false).getFirst();
        assertFalse(hiddenMatch.concealed());
        assertTrue(hiddenMatch.washedMatch());
        assertEquals("Public title", hiddenMatch.title());
        assertEquals("", hiddenMatch.snippet());
        var visibleMatch = BookSearch.query(List.of(partial), "visible", level -> false).getFirst();
        assertFalse(visibleMatch.snippet().contains("secret"));
        assertFalse(visibleMatch.snippet().contains("signal"));
        assertFalse(BookSearch.query(List.of(partial), "signal", level -> true).getFirst().washedMatch());
    }

    @Test
    void filteringTheInputRemovesLockedEntriesFromAllCounts() {
        var readable = content("known", "Known", "Text", 0);
        var results = BookSearch.query(List.of(readable, readable), "", level -> false);
        assertEquals(1, results.size(), "Search results are deduplicated by canonical ID");
    }

    private static BookEntryContent content(String path, String title, String text, int level) {
        var json = new com.google.gson.JsonObject();
        json.addProperty("ordinality", 0); json.addProperty("texture", "test:page");
        json.addProperty("title", title); json.addProperty("subtitle", "");
        json.addProperty("text", text); json.addProperty("icon", "minecraft:book"); json.addProperty("revealLevel", level);
        var page = PageTemplate.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        page.setId(ResourceLocation.parse("test:book/intro/pages/" + path));
        return BookEntryContent.localize(page, ResourceLocation.parse("test:book/intro/chapter"), Function.identity());
    }
}
