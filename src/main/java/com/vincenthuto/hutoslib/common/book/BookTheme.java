package com.vincenthuto.hutoslib.common.book;

import net.minecraft.resources.Identifier;

/**
 * Declares the visual identity of a book: which textures to use for the
 * background and tabs, and what accent colour to apply to highlights.
 *
 * <p>Attach a {@code BookTheme} to a {@link com.vincenthuto.hutoslib.common.data.book.BookCodeModel}
 * via {@link com.vincenthuto.hutoslib.common.data.book.BookCodeModel#setTheme(BookTheme)} to
 * give a book its own look. When {@code null} the screens fall back to their
 * existing default textures and white (#FFFFFF) accents.
 *
 * <p>Example – a crimson-themed book:
 * <pre>{@code
 * book.setTheme(new BookTheme(
 *     Identifier.fromNamespaceAndPath("hemomancy", "textures/gui/liber_bg.png"),
 *     0xAA0000,
 *     Identifier.fromNamespaceAndPath("hemomancy", "textures/gui/liber_tabs.png")
 * ));
 * }</pre>
 *
 * @param backgroundTexture the full-page background GUI texture
 * @param accentColor       RGB accent colour used for highlights (e.g. unread
 *                          badges, title text). Packed as {@code 0xRRGGBB}.
 * @param tabTexture        the sprite sheet used to draw chapter-tab buttons
 */
public record BookTheme(
        Identifier backgroundTexture,
        int accentColor,
        Identifier tabTexture) {

    /** Default white accent – used when a theme supplies an override colour of 0. */
    public static final int DEFAULT_ACCENT = 0xFFFFFF;
}
