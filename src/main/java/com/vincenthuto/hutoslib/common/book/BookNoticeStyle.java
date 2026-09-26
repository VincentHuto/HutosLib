package com.vincenthuto.hutoslib.common.book;

public record BookNoticeStyle(String discovery, String forgotten, String revealed, int attentionColor) {
    public static final BookNoticeStyle DEFAULT = new BookNoticeStyle("hutoslib.book.discovery", "hutoslib.book.forgotten", "hutoslib.book.revealed", 0xFFFFD700);
}
