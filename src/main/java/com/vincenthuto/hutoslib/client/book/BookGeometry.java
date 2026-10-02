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
        int availableHeight = screenHeight - extraHeight;
        boolean spread = !layout.spread().equals("never") && 348 + extraWidth <= screenWidth && availableHeight >= 208;
        int outerWidth = spread ? 348 : layout.pageWidth(), nativeHeight = spread ? 232 : layout.pageHeight();
        boolean compact = outerWidth + extraWidth > screenWidth || availableHeight < Math.min(208, nativeHeight);
        int outerHeight = compact ? nativeHeight : Math.min(nativeHeight, availableHeight);
        int viewportWidth = compact ? Math.min(outerWidth, Math.max(40, screenWidth - 8)) : outerWidth;
        int viewportHeight = compact ? Math.min(outerHeight, Math.max(64, screenHeight - 8)) : outerHeight;
        int leading = layout.tabStyle().equals("spine") ? side : 0;
        int left = compact ? (screenWidth - viewportWidth) / 2 : (screenWidth - outerWidth - extraWidth) / 2 + leading + 21;
        int top = compact ? (screenHeight - viewportHeight) / 2 : (screenHeight - outerHeight - extraHeight) / 2 + head + 7;
        return new BookGeometry(spread, compact, left, top, outerWidth, outerHeight,
                spread ? 170 : layout.pageWidth(), compact ? viewportHeight : spread ? outerHeight - 8 : outerHeight, viewportWidth, viewportHeight,
                Math.max(12, (compact ? viewportWidth : spread ? 170 : layout.pageWidth()) - 28));
    }
}
