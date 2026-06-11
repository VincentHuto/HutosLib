package com.vincenthuto.hutoslib.client.screen.template;

import com.vincenthuto.hutoslib.common.network.PacketEffectTemplateItem;
import com.vincenthuto.hutoslib.common.template.EffectTemplateType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class EffectTemplateScreen extends Screen {
	private final InteractionHand hand;
	private final EffectTemplateType type;
	private String initialJson;
	private EditBox input;
	private String status = "";
	private int statusColor = 0xA0FFA0;

	private EffectTemplateScreen(InteractionHand hand, EffectTemplateType type, String initialJson) {
		super(Component.literal(templateTitle(type)));
		this.hand = hand;
		this.type = type;
		this.initialJson = initialJson;
	}

	public static void open(InteractionHand hand, EffectTemplateType type, ItemStack stack) {
		Minecraft.getInstance().setScreen(new EffectTemplateScreen(hand, type, type.jsonFromItem(stack)));
	}

	@Override
	protected void init() {
		int fieldWidth = Math.min(width - 40, 520);
		int left = (width - fieldWidth) / 2;
		input = new EditBox(font, left, 42, fieldWidth, 20, Component.literal("Template JSON"));
		input.setMaxLength(32767);
		input.setValue(initialJson);
		addRenderableWidget(input);

		int buttonY = height - 28;
		addRenderableWidget(Button.builder(Component.literal("Save"), button -> save()).bounds(width / 2 - 126,
				buttonY, 76, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Reset"), button -> reset()).bounds(width / 2 - 38,
				buttonY, 76, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose()).bounds(width / 2 + 50,
				buttonY, 76, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 14, 0xFFFFFF);
		graphics.drawCenteredString(font, "Paste or edit one-line template JSON", width / 2, 28, 0xC0C0C0);
		if (!status.isEmpty()) {
			graphics.drawCenteredString(font, status, width / 2, 68, statusColor);
		}
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void reset() {
		initialJson = type.defaultJson();
		input.setValue(initialJson);
		status = "Reset to defaults";
		statusColor = 0xA0FFA0;
	}

	private void save() {
		String json = input.getValue();
		try {
			type.validateJson(json);
		} catch (RuntimeException exception) {
			status = "Invalid JSON: " + exception.getMessage();
			statusColor = 0xFF7777;
			return;
		}
		PacketDistributor.sendToServer(new PacketEffectTemplateItem(hand, type, json));
		initialJson = json;
		status = "Saved";
		statusColor = 0xA0FFA0;
	}

	private static String templateTitle(EffectTemplateType type) {
		return switch (type) {
		case LIGHTNING -> "Lightning Template";
		case TENDRIL -> "Tendril Template";
		};
	}
}
