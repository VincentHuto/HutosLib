package com.vincenthuto.hutoslib.client.screen;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class BannerSlotScreen extends AbstractRecipeBookScreen<BannerSlotContainer> {
	private static final Identifier SCREEN_BACKGROUND = HutosLib.rloc(
			"textures/gui/banner_slot.png");
	private float oldMouseX;
	private float oldMouseY;

	public BannerSlotScreen(BannerSlotContainer container, Inventory playerInventory, Component title) {
		super(container, new CraftingRecipeBookComponent(container), playerInventory, title);
		this.titleLabelX = 97;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected ScreenPosition getRecipeBookButtonPosition() {
		return new ScreenPosition(this.leftPos + 104, this.height / 2 - 22);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.oldMouseX = (float) mouseX;
		this.oldMouseY = (float) mouseY;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = this.topPos;
		graphics.blit(RenderPipelines.GUI_TEXTURED, SCREEN_BACKGROUND, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight,
				256, 256);
		Player player = this.minecraft != null ? this.minecraft.player : null;
		if (player != null) {
			InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F,
					this.oldMouseX, this.oldMouseY, player);
		}
	}


}