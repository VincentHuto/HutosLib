package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.book.BookVisualTheme;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

/** The host owns the header/footer and clips this native-unit body viewport, including scrolled content. */
public record BookPageRenderContext(GuiGraphics graphics, Font font, PageTemplate page, BookCodeModel book,
                                    BookVisualTheme theme, Bounds body, int mouseX, int mouseY, float partialTick,
                                    double yaw, double pitch, int scrollOffset, Consumer<ResourceLocation> navigate) {
    public record Bounds(int x, int y, int width, int height) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
}
