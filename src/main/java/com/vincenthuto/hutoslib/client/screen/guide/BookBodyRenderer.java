package com.vincenthuto.hutoslib.client.screen.guide;

/** Custom body renderers receive measured bounds rather than absolute coordinates from an old screen. */
@FunctionalInterface
public interface BookBodyRenderer {
    void render(BookPageRenderContext context);
    default int contentHeight(BookPageRenderContext context) { return context.body().height(); }
    default boolean mouseClicked(BookPageRenderContext context, double x, double y, int button) { return false; }
    default boolean mouseDragged(BookPageRenderContext context, double x, double y, int button, double dx, double dy) { return false; }
}
