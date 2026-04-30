package com.vincenthuto.hutoslib.common.book.filter;

import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;

import net.minecraft.world.entity.player.Player;

/**
 * Strategy interface that decides which pages/chapters of a {@link BookCodeModel}
 * are visible to a given player.
 *
 * <p>Mods implement this to hide locked content (e.g. Hemomancy's
 * {@code MemoBookFilter}). The no-op default is available as
 * {@link BookCodeModel#UNFILTERED}.
 *
 * <p>The filter is applied immediately before a book screen is opened; the
 * original {@link BookCodeModel} is never mutated.
 */
@FunctionalInterface
public interface IBookPageFilter {
    /**
     * Returns a (possibly new) {@link BookCodeModel} containing only the
     * chapters and pages that {@code player} should be allowed to see.
     *
     * <p>Implementations are free to return {@code source} unchanged when no
     * filtering is needed.
     *
     * @param source the full, unfiltered book model
     * @param player the player opening the book
     * @return the filtered (or identical) book model; never {@code null}
     */
    BookCodeModel filter(BookCodeModel source, Player player);
}
