package com.vincenthuto.hutoslib.client.screen.guide;

import com.vincenthuto.hutoslib.client.book.BookEntryContent;
import com.vincenthuto.hutoslib.client.screen.HLGuiUtils;
import com.vincenthuto.hutoslib.client.screen.ScreenBlockTintGetter;
import com.vincenthuto.hutoslib.common.data.book.CraftingRecipeTemplate;
import com.vincenthuto.hutoslib.math.MultiblockPattern;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

/** Adapts the existing recipe and model algorithms to the new clipped body viewport. */
public final class BookBuiltinRenderers {
    private BookBuiltinRenderers() {}

    public static final BookBodyRenderer CRAFTING = new BookBodyRenderer() {
        @Override public void render(BookPageRenderContext context) {
            if (!(context.page() instanceof CraftingRecipeTemplate recipe)) return;
            var body = context.body();
            recipe.getItemRecipe().render(context.graphics(), Minecraft.getInstance(), body.x(), body.y() - 31 - context.scrollOffset(), true, context.partialTick());
            int y = body.y() + 72 - context.scrollOffset();
            for (var line : context.font().split(Component.literal(sanitizedBody(context)), body.width())) {
                context.graphics().drawString(context.font(), line, body.x(), y, context.theme().color("ink"), false);
                y += 9;
            }
        }
        @Override public int contentHeight(BookPageRenderContext context) {
            return 72 + context.font().split(Component.literal(sanitizedBody(context)), context.body().width()).size() * 9;
        }
    };

    public static BookBodyRenderer multiblock(MultiblockPattern pattern) {
        return new BookBodyRenderer() {
            @Override public void render(BookPageRenderContext context) { renderMultiblock(context, pattern, 0); }
            @Override public int contentHeight(BookPageRenderContext context) { return 110 + pattern.getMaterialCounts(false).size() * 10; }
        };
    }

    public static void renderMultiblock(BookPageRenderContext context, MultiblockPattern pattern, int topInset) {
        var graphics = context.graphics();
        var body = context.body();
        int y = body.y() + topInset - context.scrollOffset();
        long cycleIndex = System.currentTimeMillis() / 2000L;
        for (var material : pattern.getMaterialCounts(false)) {
            var key = material.key();
            var block = key.displayBlock(cycleIndex);
            String name = key.isTag() ? key.displayLabel() + " (" + I18n.get(block.getDescriptionId()) + ")" : I18n.get(block.getDescriptionId());
            graphics.drawString(context.font(), Component.literal(name + ": " + material.count()), body.x(), y, context.theme().color("ink"), false);
            y += 10;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(body.x() + body.width() / 2.0, y + 54, 0);
        graphics.pose().mulPose(Vector3.XN.rotationDegrees(-45 + (float) context.pitch()).toMoj());
        graphics.pose().mulPose(Vector3.YP.rotationDegrees(45 + (float) context.yaw()).toMoj());
        int maxDimension = Math.max(pattern.getBlockPattern().getWidth(), Math.max(pattern.getBlockPattern().getHeight(), pattern.getBlockPattern().getDepth()));
        float scale = Math.min(2f, body.width() / (Math.max(1,maxDimension) * 14f));
        graphics.pose().scale(scale,scale,scale);
        HLGuiUtils.renderMultiBlock(graphics.pose(), pattern, context.partialTick(), new ScreenBlockTintGetter(), 0, 0);
        graphics.pose().popPose();
    }

    private static String sanitizedBody(BookPageRenderContext context) {
        var player = Minecraft.getInstance().player;
        var localized = BookEntryContent.localize(context.page(), context.page().getId(), value -> I18n.get(value));
        return localized.sanitizedText(level -> context.book().canReveal(player, level));
    }
}
