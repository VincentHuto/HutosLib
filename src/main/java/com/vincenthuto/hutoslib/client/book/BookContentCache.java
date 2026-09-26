package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public final class BookContentCache {
    private final Map<ResourceLocation, BookEntryContent> entries = new HashMap<>();
    private final Map<String, com.vincenthuto.hutoslib.common.book.BookText> text = new HashMap<>();
    private long dataGeneration = -1, resourceGeneration = -1;
    private String language = "";

    public BookEntryContent get(PageTemplate page, ResourceLocation chapter) {
        invalidate();
        return entries.computeIfAbsent(page.getId(), ignored -> BookEntryContent.localize(page, chapter, value -> I18n.get(value)));
    }

    public com.vincenthuto.hutoslib.common.book.BookText text(String source) {
        invalidate();
        return text.computeIfAbsent(source, value -> com.vincenthuto.hutoslib.common.book.BookText.parse(I18n.get(value)));
    }

    private void invalidate() {
        long data = BookPlaceboReloadListener.INSTANCE.getGeneration(), resources = BookThemeManager.INSTANCE.generation();
        String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
        if (data != dataGeneration || resources != resourceGeneration || !language.equals(currentLanguage)) {
            entries.clear();
            text.clear();
            dataGeneration = data;
            resourceGeneration = resources;
            language = currentLanguage;
        }
    }
}
