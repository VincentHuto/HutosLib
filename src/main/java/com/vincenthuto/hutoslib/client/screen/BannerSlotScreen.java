package com.vincenthuto.hutoslib.client.screen;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.container.BannerSlotContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BannerSlotScreen extends AbstractContainerScreen<BannerSlotContainer> {
	private static final Identifier SCREEN_BACKGROUND = HutosLib.rloc("textures/gui/banner_slot.png");

	public BannerSlotScreen(BannerSlotContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		this.titleLabelX = 97;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(graphics, mouseX, mouseY, partialTicks);
		graphics.blit(RenderPipelines.GUI_TEXTURED, SCREEN_BACKGROUND, this.leftPos, this.topPos, 0, 0,
				this.imageWidth, this.imageHeight, 256, 256);
		if (this.minecraft != null && this.minecraft.player != null) {
			InventoryScreen.extractEntityInInventoryFollowsMouse(graphics,
					this.leftPos + 26, this.topPos + 8,
					this.leftPos + 75, this.topPos + 78,
					30, 0.0625F, mouseX, mouseY, this.minecraft.player);
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}
}
