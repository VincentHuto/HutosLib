package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.book.BookText;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;

/** Localized source syntax. Viewer-specific concealment is applied at the point of use. */
public record BookEntryContent(PageTemplate page, ResourceLocation chapterId, String title, String subtitle,
                               String recordLabel, Map<String, BookText> blocks) {
    public BookEntryContent { blocks = Collections.unmodifiableMap(new LinkedHashMap<>(blocks)); }

    public static BookEntryContent localize(PageTemplate page, ResourceLocation chapterId, Function<String, String> translate) {
        Map<String, BookText> blocks = new LinkedHashMap<>();
        var extra = page.getPresentation();
        extra.record().ifPresent(record -> blocks.put("record", BookText.parse(translate.apply(record.text()))));
        blocks.put("body", BookText.parse(translate.apply(page.getText())));
        if (!extra.margin().isEmpty()) blocks.put("margin", BookText.parse(translate.apply(extra.margin())));
        for (int i = 0; i < extra.footnotes().size(); i++)
            blocks.put("footnote/" + i, BookText.parse(translate.apply(extra.footnotes().get(i))));
        return new BookEntryContent(page, chapterId, translate.apply(page.getTitle()), translate.apply(page.getSubtitle()),
                extra.record().map(record -> translate.apply(record.label())).orElse(""), blocks);
    }

    public boolean readable(IntPredicate access) {
        int level = page.getPresentation().revealLevel();
        return level == 0 || (level > 0 && access.test(level));
    }

    public Set<Integer> revealedLevels(IntPredicate access) {
        if (!readable(access)) return Set.of();
        Set<Integer> levels = new HashSet<>();
        int whole = page.getPresentation().revealLevel();
        if (whole > 0) levels.add(whole);
        blocks.values().forEach(text -> text.runs().stream().filter(run -> run.readable(access))
                .forEach(run -> levels.addAll(run.levels())));
        return Set.copyOf(levels);
    }

    public String sanitizedText(IntPredicate access) {
        if (!readable(access)) return "";
        return String.join("\n", blocks.values().stream().map(text -> text.sanitized(access)).toList());
    }

    String searchableSource() {
        return title + "\n" + subtitle + "\n" + String.join("\n", blocks.values().stream().map(BookText::plainText).toList());
    }
}
