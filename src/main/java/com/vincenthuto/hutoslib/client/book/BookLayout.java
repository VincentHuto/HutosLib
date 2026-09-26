package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.client.screen.guide.BookBodyRenderer;

public record BookLayout(Kind kind, BookBodyRenderer renderer) {
    public enum Kind { COVER, CONTENTS, CHAPTER, SLOTS, TEXT, RECORD, PREVIEW, SEARCH, GLOSSARY, CUSTOM }
    public static BookLayout custom(BookBodyRenderer renderer) { return new BookLayout(Kind.CUSTOM, java.util.Objects.requireNonNull(renderer)); }
}
