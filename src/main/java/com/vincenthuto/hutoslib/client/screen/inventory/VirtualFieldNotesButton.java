package com.vincenthuto.hutoslib.client.screen.inventory;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.FieldNotes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VirtualFieldNotesButton extends Button {
	private static final int WIDTH = 20;
	private static final int HEIGHT = 18;
	private static final int BUTTON_TEXTURE_WIDTH = WIDTH * 2;
	private static final int BOOK_WIDTH = 14;
	private static final int BOOK_HEIGHT = 12;
	private static final ResourceLocation BUTTON_TEXTURE = HutosLib.rloc("textures/gui/virtual_field_notes_button.png");
	private static final ResourceLocation BOOK_TEXTURE = HutosLib.rloc("textures/gui/virtual_field_notes_book.png");
	private static final Component TITLE = Component.translatable("screen.hutoslib.virtual_field_notes.title");
	private final InventoryScreen screen;

	public VirtualFieldNotesButton(InventoryScreen screen) {
		super(screen.getGuiLeft() + 128, screen.height / 2 - 22, WIDTH, HEIGHT, TITLE, ignored -> {
		}, DEFAULT_NARRATION);
		this.screen = screen;
	}

	@Override
	public void onPress() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null) {
			minecraft.player.displayClientMessage(
					Component.translatable("screen.hutoslib.virtual_field_notes.status")
							.withStyle(ChatFormatting.GRAY), true);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		setX(screen.getGuiLeft() + 128);
		setY(screen.height / 2 - 22);
		int x = getX();
		int y = getY();
		int textureX = isHoveredOrFocused() ? WIDTH : 0;
		graphics.blit(BUTTON_TEXTURE, x, y, textureX, 0, WIDTH, HEIGHT, BUTTON_TEXTURE_WIDTH, HEIGHT);
		graphics.blit(BOOK_TEXTURE, x + 3, y + 3, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT);

		int total = totalPendingMemos();
		if (total > 0) {
			Font font = Minecraft.getInstance().font;
			String count = total > 99 ? "99+" : Integer.toString(total);
			graphics.drawString(font, count, x + WIDTH - 1 - font.width(count), y + 9, 0xFFFFFFFF, true);
		}

		if (isHovered()) {
			graphics.renderTooltip(Minecraft.getInstance().font, tooltipLines(), Optional.empty(), mouseX, mouseY);
		}
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		defaultButtonNarrationText(narrationElementOutput);
	}

    private static int totalPendingMemos() {
        var player = Minecraft.getInstance().player;
        return FieldNotes.available(player).stream().mapToInt(provider -> provider.pending(player)).sum();
    }
    private static List<Component> tooltipLines() {
        var player = Minecraft.getInstance().player;
        List<Component> lines = new ArrayList<>();
        lines.add(TITLE.copy().withStyle(ChatFormatting.WHITE));
        lines.add(Component.translatable("screen.hutoslib.virtual_field_notes.total", totalPendingMemos()));
        FieldNotes.available(player).forEach(provider -> lines.addAll(provider.tooltip(player)));
        return lines;
    }
}
