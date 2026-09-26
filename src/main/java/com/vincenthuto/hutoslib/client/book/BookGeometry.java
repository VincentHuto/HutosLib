package com.vincenthuto.hutoslib.client.book;

/** Native GUI-unit bounds, including the largest active tab, frame, shadow, ribbon and margin. */
public record BookGeometry(boolean spread, boolean compact, int left, int top, int outerWidth, int outerHeight,
                           int leafWidth, int leafHeight, int viewportWidth, int viewportHeight, int textWidth) {
    public static BookGeometry fit(int screenWidth, int screenHeight, BookVisualTheme.Layout layout, int chapters) {
        boolean sideTabs = chapters > 0 && (layout.tabStyle().equals("fore_edge") || layout.tabStyle().equals("spine"));
        int side = sideTabs ? 24 : 0;
        int head = chapters > 0 && layout.tabStyle().equals("head") ? 22 : 0;
        int extraWidth = side + 10 + 8 + 14;
        int extraHeight = head + 10 + 6 + 8;
        boolean spread = !layout.spread().equals("never") && 348 + extraWidth <= screenWidth && 232 + extraHeight <= screenHeight;
        int outerWidth = spread ? 348 : layout.pageWidth(), outerHeight = spread ? 232 : layout.pageHeight();
        boolean compact = outerWidth + extraWidth > screenWidth || outerHeight + extraHeight > screenHeight;
        int viewportWidth = compact ? Math.max(40, screenWidth - 8) : outerWidth;
        int viewportHeight = compact ? Math.max(64, screenHeight - 8) : outerHeight;
        int leading = layout.tabStyle().equals("spine") ? side : 0;
        int left = compact ? 4 : (screenWidth - outerWidth - extraWidth) / 2 + leading + 21;
        int top = compact ? 4 : (screenHeight - outerHeight - extraHeight) / 2 + head + 7;
        return new BookGeometry(spread, compact, left, top, outerWidth, outerHeight,
                spread ? 170 : layout.pageWidth(), spread ? 224 : layout.pageHeight(), viewportWidth, viewportHeight,
                Math.max(12, (compact ? viewportWidth : spread ? 170 : layout.pageWidth()) - 28));
    }
}
