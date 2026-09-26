package com.vincenthuto.hutoslib.common.data.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.vincenthuto.hutoslib.common.book.BookText;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/** Immutable identities and authored versions, built before any viewer filtering. */
public record BookSourceIndex(Map<ResourceLocation, Entry> entries, Map<ResourceLocation, Integer> chapters,
                              List<ChapterTemplate> sourceChapters) {
    public BookSourceIndex(Map<ResourceLocation, Entry> entries, Map<ResourceLocation, Integer> chapters) {
        this(entries, chapters, List.of());
    }
    public static final BookSourceIndex EMPTY = new BookSourceIndex(Map.of(), Map.of());

    public BookSourceIndex {
        entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
        chapters = Map.copyOf(chapters);
        sourceChapters = List.copyOf(sourceChapters);
    }

    public record Entry(ResourceLocation id, ResourceLocation chapterId, int chapterNumber, int entryNumber,
                        String version, Map<String, BookText> blocks) {
        public Entry { blocks = Collections.unmodifiableMap(new LinkedHashMap<>(blocks)); }
    }

    public Optional<Entry> entry(ResourceLocation id) { return Optional.ofNullable(entries.get(id)); }

    public static BookSourceIndex create(List<ChapterTemplate> chapters) {
        Map<ResourceLocation, Entry> entries = new LinkedHashMap<>();
        Map<ResourceLocation, Integer> chapterNumbers = new LinkedHashMap<>();
        int chapterNumber = 0;
        for (ChapterTemplate chapter : chapters) {
            chapterNumbers.put(chapter.getId(), ++chapterNumber);
            for (BookDataTemplate template : chapter.getPages()) {
                if (template instanceof PageTemplate page) {
                    entries.put(page.getId(), new Entry(page.getId(), chapter.getId(), chapterNumber, entries.size() + 1,
                            contentVersion(page), sourceBlocks(page)));
                }
            }
        }
        return new BookSourceIndex(entries, chapterNumbers, chapters);
    }

    public static Map<String, BookText> sourceBlocks(PageTemplate page) {
        Map<String, BookText> blocks = new LinkedHashMap<>();
        var presentation = page.getPresentation();
        presentation.record().ifPresent(record -> blocks.put("record", BookText.parse(record.text())));
        blocks.put("body", BookText.parse(page.getText()));
        if (!presentation.margin().isEmpty()) blocks.put("margin", BookText.parse(presentation.margin()));
        for (int i = 0; i < presentation.footnotes().size(); i++)
            blocks.put("footnote/" + i, BookText.parse(presentation.footnotes().get(i)));
        return blocks;
    }

    public static String contentVersion(PageTemplate page) {
        JsonObject source = PageTemplate.CODEC.encodeStart(JsonOps.INSTANCE, page).getOrThrow().getAsJsonObject();
        JsonObject content = new JsonObject();
        for (String field : List.of("title", "subtitle", "text", "margin", "record", "footnotes", "seeAlso", "icon", "revealLevel", "requiresEntry")) {
            if (source.has(field)) content.add(field, canonical(source.get(field)));
        }
        return digest(content);
    }

    public static String surfaceVersion(BookDataTemplate template) {
        JsonObject source;
        if(template instanceof BookTemplate book) source=BookTemplate.CODEC.encodeStart(JsonOps.INSTANCE,book).getOrThrow().getAsJsonObject();
        else if(template instanceof ChapterTemplate chapter) source=ChapterTemplate.CODEC.encodeStart(JsonOps.INSTANCE,chapter).getOrThrow().getAsJsonObject();
        else return contentVersion((PageTemplate)template);
        JsonObject content=new JsonObject();
        for(String field:List.of("title","subtitle","text","margin","callout","icon"))if(source.has(field))content.add(field,source.get(field));
        return digest(content);
    }

    private static String digest(JsonObject content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical(content).toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    private static JsonElement canonical(JsonElement value) {
        if (value.isJsonObject()) {
            JsonObject result = new JsonObject();
            new TreeSet<>(value.getAsJsonObject().keySet()).forEach(key -> result.add(key, canonical(value.getAsJsonObject().get(key))));
            return result;
        }
        if (value.isJsonArray()) {
            JsonArray result = new JsonArray();
            value.getAsJsonArray().forEach(element -> result.add(canonical(element)));
            return result;
        }
        return value;
    }
}
