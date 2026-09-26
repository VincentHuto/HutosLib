package com.vincenthuto.hutoslib.client.book;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class BookThemeResolverTest {
    @Test
    void unreadTitleShiftInheritsAllowsPeriodOverridesAndCanBeDisabled() {
        var themes = BookThemeResolver.resolve(Map.of(
                id("test:parent"), json("""
                    {"unreadTitleShift":{"from":"#B3121A","to":"#7A248F","periodTicks":60}}
                    """),
                id("test:child"), json("""
                    {"parent":"test:parent","unreadTitleShift":{"periodTicks":100}}
                    """),
                id("test:off"), json("""
                    {"parent":"test:child","unreadTitleShift":null}
                    """)), ignored -> true, ignored -> {});
        var theme = themes.get(id("test:child"));
        var shift = theme.unreadTitleShift();
        assertEquals(0xFFB3121A, shift.colorAt(0));
        assertEquals(0xFF7A248F, shift.colorAt(2500));
        assertEquals(shift.colorAt(0), shift.colorAt(5000));
        assertEquals(shift.colorAt(1250), shift.colorAt(3750));
        assertEquals(theme.color("ink"), theme.entryTitleColor(false, false, 2500));
        assertEquals(theme.color("inkRead"), theme.entryTitleColor(true, false, 2500));
        assertEquals(shift.colorAt(2500), theme.entryTitleColor(false, true, 2500));
        assertNotEquals(shift.colorAt(2500), theme.entryTitleColor(true, true, 2500));
        assertNull(themes.get(id("test:off")).unreadTitleShift());
        assertNull(themes.get(BookThemeResolver.DEFAULT_ID).unreadTitleShift());
        assertThrows(IllegalArgumentException.class, () -> new BookVisualTheme.ColorShift(0, 0, 0));
    }
    @Test
    void legacyBooksRetainSeparateCoverOverlayChapterAndExplicitTabTextures() {
        var template = new com.vincenthuto.hutoslib.common.data.book.BookTemplate(
                "test:cover.png", "test:overlay.png", "Title", "Subtitle", "", "minecraft:book");
        var book = new com.vincenthuto.hutoslib.common.data.book.BookCodeModel(id("test:book"), template);
        var chapter = new com.vincenthuto.hutoslib.common.data.book.ChapterTemplate(
                0, "test:chapter.png", "1,1,1", "Chapter", "", "minecraft:book");
        var cover = BookThemeManager.INSTANCE.resolve(book, null);
        assertEquals(id("test:cover.png"), cover.texture("cover"));
        assertEquals(id("test:overlay.png"), cover.texture("overlay"));
        assertEquals(id("test:chapter.png"), BookThemeManager.INSTANCE.resolve(book, null, chapter).texture("page"));
        book.setTheme(new com.vincenthuto.hutoslib.common.book.BookTheme(id("test:override.png"), 0xAA0000, id("test:tabs.png")));
        cover = BookThemeManager.INSTANCE.resolve(book, null);
        assertEquals(id("test:cover.png"), cover.texture("cover"));
        assertEquals(id("test:override.png"), cover.texture("overlay"));
        assertEquals(id("test:tabs.png"), cover.legacyTabs());
        assertEquals("never", cover.layout().spread());
    }
    @Test
    void derivedColorsUseTheChildBasesAndOptionalFieldsCanBeCleared() {
        var themes = BookThemeResolver.resolve(Map.of(
                id("test:parent"), json("""
                {"colors":{"paper":"#FFFFFF","ink":"#000000","accent":"#FF0000"},
                 "fonts":{"hand":"test:hand"},"textures":{"decals":["test:stain.png"]}}
                """),
                id("test:child"), json("""
                {"parent":"test:parent","colors":{"accent":"#0000FF"},
                 "fonts":{"hand":null,"body":null},"textures":{"decals":[]}}
                """)), ignored -> true, ignored -> {});
        var child = themes.get(id("test:child"));
        assertEquals(0xFF0000FF, child.color("link"));
        assertNotEquals(themes.get(id("test:parent")).color("accentHi"), child.color("accentHi"));
        assertNull(child.font("hand"));
        assertEquals(id("minecraft:default"), child.font("body"));
        assertTrue(child.decals().isEmpty());
    }

    @Test
    void currentAtlasWinsOverAParentRegionOverride() {
        var themes = BookThemeResolver.resolve(Map.of(
                id("test:parent"), json("{\"sprites\":{\"N\":\"test:arrows.png\"}}"),
                id("test:child"), json("{\"parent\":\"test:parent\",\"textures\":{\"ui\":\"test:ui.png\"}}")),
                ignored -> true, ignored -> {});
        assertEquals(id("test:ui.png"), themes.get(id("test:child")).sprite(BookAtlas.N).texture());
        assertEquals(id("test:arrows.png"), themes.get(id("test:parent")).sprite(BookAtlas.N).texture());
        assertEquals(0, themes.get(id("test:parent")).sprite(BookAtlas.N).u());
        assertEquals(72, themes.get(id("test:parent")).sprite(BookAtlas.N).textureWidth());
    }

    @Test
    void nullRegionOverrideClearsTheParentOverrideAndRetainsItsAtlas() {
        var themes = BookThemeResolver.resolve(Map.of(
                id("test:parent"), json("{\"sprites\":{\"N\":\"test:arrows.png\"},\"textures\":{\"ui\":\"test:parent_ui.png\"}}"),
                id("test:child"), json("{\"parent\":\"test:parent\",\"sprites\":{\"N\":null}}")),
                ignored -> true, ignored -> {});
        assertEquals(id("test:parent_ui.png"), themes.get(id("test:child")).sprite(BookAtlas.N).texture());
    }

    @Test
    void missingParentsCyclesAndMissingTexturesFallBackWithoutBreakingOtherThemes() {
        var warnings = new ArrayList<String>();
        var themes = BookThemeResolver.resolve(Map.of(
                id("test:a"), json("{\"parent\":\"test:b\"}"),
                id("test:b"), json("{\"parent\":\"test:a\"}"),
                id("test:c"), json("{\"parent\":\"test:missing\"}"),
                id("test:valid"), json("{\"textures\":{\"page\":\"test:missing.png\"},\"colors\":{\"accent\":\"#123456\"}}")),
                location -> !location.getPath().equals("missing.png"), warnings::add);
        var fallback = themes.get(BookThemeResolver.DEFAULT_ID);
        assertSame(fallback, themes.get(id("test:a")));
        assertSame(fallback, themes.get(id("test:b")));
        assertSame(fallback, themes.get(id("test:c")));
        assertEquals(fallback.texture("page"), themes.get(id("test:valid")).texture("page"));
        assertEquals(0xFF123456, themes.get(id("test:valid")).color("accent"));
        assertFalse(warnings.isEmpty());
    }

    private static ResourceLocation id(String id) { return ResourceLocation.parse(id); }

    @Test
    void missingChildFontFallsBackThroughItsParent() {
        var themes=BookThemeResolver.resolve(Map.of(
                id("test:parent"),json("{\"fonts\":{\"hand\":\"test:hand\"}}"),
                id("test:child"),json("{\"parent\":\"test:parent\",\"fonts\":{\"hand\":\"test:missing\"}}")),
                location->!location.getPath().equals("font/missing.json"),ignored->{});
        assertEquals(id("test:hand"),themes.get(id("test:child")).font("hand"));
    }
    private static JsonObject json(String json) { return JsonParser.parseString(json).getAsJsonObject(); }
}
