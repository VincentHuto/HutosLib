package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Optional custom renderer for a specific {@link BookDataTemplate} type.
 *
 * <p>Register an instance on a page template via
 * {@link BookDataTemplate#setPageRenderer(IBookPageRenderer)} to completely
 * replace the default title/subtitle/body rendering inside
 * {@link HLGuiGuidePage}. The renderer is called <em>instead of</em> the
 * default text layout, so it is responsible for drawing all page-specific
 * content.
 *
 * <p>The host screen still draws the background, navigation arrows, tab
 * buttons, and tooltips.
 */
@FunctionalInterface
public interface IBookPageRenderer {
    /**
     * Draw the custom content for {@code page} inside the book screen.
     *
     * @param graphics    the current render context
     * @param page        the data template for the page being rendered
     * @param screen      the host {@link HLGuiGuidePage} (provides {@code left},
     *                    {@code top}, {@code guiWidth}, {@code guiHeight},
     *                    and the Minecraft {@code font})
     * @param mouseX      current mouse X in screen coordinates
     * @param mouseY      current mouse Y in screen coordinates
     * @param partialTick partial tick fraction
     */
    void render(GuiGraphicsExtractor graphics, BookDataTemplate page, HLGuiGuidePage screen,
                int mouseX, int mouseY, float partialTick);
}
