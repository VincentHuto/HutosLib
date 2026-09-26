package com.vincenthuto.hutoslib.common.data.book;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookKitCodecTest {
    @Test
    void craftingPagesKeepTheSameDiscoveryAndPresentationContract() {
        JsonObject source = json("""
                {"ordinality":0,"texture":"test:page","title":"Recipe","subtitle":"",
                 "text":"Instructions","icon":"minecraft:book","requiresEntry":"test:recipe",
                 "revealLevel":2,"footnotes":["Crafting note"]}
                """);
        JsonObject encoded = roundTrip(CraftingRecipeTemplate.CODEC, source);
        for (String field : List.of("requiresEntry", "revealLevel", "footnotes")) {
            assertEquals(source.get(field), encoded.get(field), field + " was lost in the specialized page");
        }
    }

    @Test
    void glossaryTypeIsRegisteredWithTheRealReloadListener() {
        class Reader extends BookPlaceboReloadListener {
            BookDataTemplate read(JsonObject source) { return serializers.read(source); }
        }
        JsonObject source = json("""
                {"type":"hutoslib:glossary_term","book":"test:guide","term":"Witness",
                 "refs":["test:guide/intro/pages/entry"]}
                """);
        BookDataTemplate term = assertDoesNotThrow(() -> new Reader().read(source));
        assertEquals(source.get("refs"), term.getSerializer().writeUnchecked(term).get("refs"));
    }

    @Test
    void bookThemeReferenceSurvivesTheSerializedDataBoundary() {
        JsonObject source = json("""
                {"coverLoc":"test:cover","overlayLoc":"test:overlay","title":"Book",
                 "subtitle":"","text":"","icon":"minecraft:book","theme":"test:skin"}
                """);
        assertEquals(source.get("theme"), roundTrip(BookTemplate.CODEC, source).get("theme"));
    }

    @Test
    void entryPresentationAndConcealmentSurviveSerialization() {
        JsonObject source = json("""
                {"ordinality":2,"texture":"test:page","title":"Entry","subtitle":"Subtitle",
                 "text":"Body {wash:4|hidden}","icon":"minecraft:book","requiresEntry":"test:discovered",
                 "layout":"record","margin":"A note","record":{"label":"RECORD","text":"Witness"},
                 "seeAlso":["other:book/chapter/pages/entry"],"footnotes":["A footnote"],
                 "pinned":true,"revealLevel":3,"decal":false}
                """);
        JsonObject encoded = roundTrip(PageTemplate.CODEC, source);
        for (String field : List.of("layout", "margin", "record", "seeAlso", "footnotes", "pinned", "revealLevel", "decal", "requiresEntry")) {
            assertEquals(source.get(field), encoded.get(field), field + " was lost");
        }
    }

    @Test
    void visibilityFilteringKeepsChapterPresentationAndPageIdentity() {
        JsonObject source = json("""
                {"ordinality":1,"texture":"test:page","color":"1,0,0","title":"Chapter",
                 "subtitle":"Subtitle","icon":"minecraft:book","tabColor":"#6B0F0F",
                 "layout":"slots","text":"Introduction","margin":"Note","rowLimit":7,
                 "callout":{"icon":"minecraft:compass","text":"Bring a compass"}}
                """);
        ChapterTemplate chapter = ChapterTemplate.CODEC.parse(JsonOps.INSTANCE, source).getOrThrow();
        chapter.setId(ResourceLocation.parse("test:guide/intro/chapter"));
        PageTemplate visible = new PageTemplate(0, "test:page", "Visible", "", "", "minecraft:book");
        PageTemplate locked = new PageTemplate(1, "test:page", "Locked", "", "", "minecraft:book", "test:gate");
        chapter.setPages(List.of(visible, locked));
        BookCodeModel book = new BookCodeModel(ResourceLocation.parse("test:guide"),
                new BookTemplate("test:cover", "test:overlay", "Book", "", "", "minecraft:book"));
        book.setChapters(List.of(chapter));

        BookCodeModel filtered = EntryGatedBookFilter.INSTANCE.filter(book, new BookKnowledge());
        ChapterTemplate remaining = filtered.getChapters().getFirst();
        assertEquals(List.of(visible), remaining.getPages());
        assertEquals(2, chapter.getPages().size(), "Filtering must not mutate the source");
        JsonObject encoded = ChapterTemplate.CODEC.encodeStart(JsonOps.INSTANCE, remaining).getOrThrow().getAsJsonObject();
        for (String field : List.of("tabColor", "layout", "text", "margin", "rowLimit", "callout")) {
            assertEquals(source.get(field), encoded.get(field), field + " was lost through filtering");
        }
    }

    @Test
    void legacyPageStillLoadsWithoutKitFields() {
        PageTemplate page = PageTemplate.CODEC.parse(JsonOps.INSTANCE, json("""
                {"ordinality":0,"texture":"test:page","title":"Legacy","subtitle":"",
                 "text":"Still readable","icon":"minecraft:book"}
                """)).getOrThrow();
        assertEquals("Still readable", page.getText());
        assertEquals("", page.getRequiresEntry());
    }

    private static JsonObject json(String value) { return JsonParser.parseString(value).getAsJsonObject(); }

    private static <T> JsonObject roundTrip(Codec<T> codec, JsonObject source) {
        T decoded = codec.parse(JsonOps.INSTANCE, source).getOrThrow();
        return codec.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow().getAsJsonObject();
    }
}
