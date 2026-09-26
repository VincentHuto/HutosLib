package com.vincenthuto.hutoslib.client.book;

import java.util.List;
import java.util.function.Predicate;

/** Pairs consecutive eligible sheets without crossing hidden entries or dedicated spreads. */
public final class BookSpreadPairing {
    private BookSpreadPairing() {}
    public static <T> List<T> pair(List<T> sourceOrder, T selected, Predicate<T> singleReadableSheet) {
        for (int i = 0; i < sourceOrder.size(); i++) {
            T left = sourceOrder.get(i);
            if (!singleReadableSheet.test(left)) continue;
            if (i + 1 < sourceOrder.size() && singleReadableSheet.test(sourceOrder.get(i + 1))) {
                T right = sourceOrder.get(++i);
                if (left.equals(selected) || right.equals(selected)) return List.of(left, right);
            }
        }
        return List.of();
    }
}
