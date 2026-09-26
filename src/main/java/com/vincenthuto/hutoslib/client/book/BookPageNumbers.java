package com.vincenthuto.hutoslib.client.book;

import net.minecraft.resources.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Physical content pages in source order, including undiscovered entries. Blank filler leaves do not count. */
public final class BookPageNumbers {
    public record Range(int first, int count) {
        public String label() { return count == 1 ? Integer.toString(first) : first + "–" + (first + count - 1); }
        public int leafNumber(List<BookPaginator.Leaf> leaves, int leafIndex) {
            int preceding = (int) leaves.stream().limit(leafIndex).filter(leaf -> !leaf.blank()).count();
            return first + preceding;
        }
    }
    private final Map<ResourceLocation, Range> ranges = new LinkedHashMap<>();
    private int next = 1;

    public void append(ResourceLocation entry, int contentPages) {
        if (ranges.containsKey(entry)) throw new IllegalArgumentException("Duplicate entry " + entry);
        int count = Math.max(1, contentPages);
        ranges.put(entry, new Range(next, count));
        next += count;
    }
    public Range range(ResourceLocation entry) { return ranges.get(entry); }
}
