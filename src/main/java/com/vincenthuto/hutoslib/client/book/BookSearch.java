package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import java.text.Normalizer;
import java.util.*;
import java.util.function.IntPredicate;

public final class BookSearch {
    private BookSearch() {}
    public record Result(ResourceLocation entryId, ResourceLocation chapterId, String title, String snippet,
                         boolean concealed, boolean washedMatch, int requirement) {}

    /** The caller supplies only entries surviving the originating item's discovery policy. */
    public static List<Result> query(Collection<BookEntryContent> visible, String query, IntPredicate access) {
        String[] terms = normalize(query).strip().split("\\s+");
        Map<ResourceLocation, Result> results = new LinkedHashMap<>();
        for (BookEntryContent content : visible) {
            if (!matches(content.searchableSource(), terms)) continue;
            ResourceLocation id = content.page().getId();
            if (!content.readable(access)) {
                results.putIfAbsent(id, new Result(id, content.chapterId(), "", "", true, true, content.page().getPresentation().revealLevel()));
                continue;
            }
            String sanitized = content.sanitizedText(access);
            boolean readableMatch = matches(content.title() + "\n" + content.subtitle() + "\n" + sanitized, terms);
            String snippet = readableMatch ? snippet(sanitized) : "";
            results.putIfAbsent(id, new Result(id, content.chapterId(), content.title(), snippet, false, !readableMatch, 0));
        }
        return List.copyOf(results.values());
    }

    public static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }

    private static boolean matches(String text, String[] terms) {
        String normalized = normalize(text);
        return Arrays.stream(terms).allMatch(normalized::contains);
    }

    private static String snippet(String sanitized) {
        String collapsed = sanitized.replaceAll("\\s+", " ").strip();
        if (collapsed.codePointCount(0, collapsed.length()) <= 96) return collapsed;
        return collapsed.substring(0, collapsed.offsetByCodePoints(0, 95)) + "…";
    }
}
