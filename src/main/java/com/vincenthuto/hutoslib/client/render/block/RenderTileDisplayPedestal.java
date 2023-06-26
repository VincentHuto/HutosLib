package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderTileDisplayPedestal implements BlockEntityRenderer<DisplayPedestalBlockEntity> {
	   private final ItemRenderer itemRenderer;

	public RenderTileDisplayPedestal(BlockEntityRendererProvider.Context pContext) {
	      this.itemRenderer = pContext.getItemRenderer();

	}

	@Override
	public void render(DisplayPedestalBlockEntity te, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		

		renderItems(te, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);

	}


	
	public void renderItems(DisplayPedestalBlockEntity te, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {		
		int items = 0;
		for (int i = 0; i < te.inventorySize(); i++)
			if (te.getItemHandler().getItem(i).isEmpty())
				break;
			else
				items++;
		float[] angles = new float[te.inventorySize()];

		float anglePer = 360F / items;
		float totalAngle = 0F;
		for (int i = 0; i < angles.length; i++)
			angles[i] = totalAngle += anglePer;

		Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS);
		for (int i = 0; i < te.inventorySize(); i++) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.5F, 1.55F, 0.5F);
			matrixStackIn.mulPose(Vector3.YP.rotationDegrees(angles[i] + te.getLevel().getGameTime()).toMoj()); // Edit
			matrixStackIn.translate(0.025F, -0.5F, 0.025F);
			matrixStackIn.mulPose(Vector3.YP.rotationDegrees(90f).toMoj()); // Edit Radius Movement
			matrixStackIn.translate(0D, 0.175D + i * 0.25, 0F); // Block/Item Scale
			matrixStackIn.scale(0.5f, 0.5f, 0.5f);
			ItemStack stack = te.getItemHandler().getItem(i);
			if (!stack.isEmpty()) {
				  this.itemRenderer.renderStatic(null, stack, ItemDisplayContext.FIXED, true, matrixStackIn, bufferIn, null,
						combinedLightIn, combinedOverlayIn, i);
			}
			matrixStackIn.popPose();
		}

	}

}
