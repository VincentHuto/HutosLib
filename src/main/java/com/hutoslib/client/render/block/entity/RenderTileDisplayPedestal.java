package com.hutoslib.client.render.block.entity;

import com.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemStack;

public class RenderTileDisplayPedestal implements BlockEntityRenderer<DisplayPedestalBlockEntity> {
	public RenderTileDisplayPedestal(BlockEntityRenderDispatcher rendererDispatcherIn) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(DisplayPedestalBlockEntity te, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		int items = 0;
		for (int i = 0; i < te.getSizeInventory(); i++)
			if (te.getItemHandler().getStackInSlot(i).isEmpty())
				break;
			else
				items++;
		float[] angles = new float[te.getSizeInventory()];

		float anglePer = 360F / items;
		float totalAngle = 0F;
		for (int i = 0; i < angles.length; i++)
			angles[i] = totalAngle += anglePer;

		Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS);
		for (int i = 0; i < te.getSizeInventory(); i++) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.5F, 1.55F, 0.5F);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(angles[i] + (float) te.getLevel().getGameTime()));
			// Edit True Radius
			matrixStackIn.translate(0.025F, -0.5F, 0.025F);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90f));
			// Edit Radius Movement
			matrixStackIn.translate(0D, 0.175D + i * 0.25, 0F);
			// Block/Item Scale
			matrixStackIn.scale(0.5f, 0.5f, 0.5f);
			ItemStack stack = te.getItemHandler().getStackInSlot(i);
			Minecraft mc = Minecraft.getInstance();
			if (!stack.isEmpty()) {

				mc.getItemRenderer().render(stack, TransformType.FIXED, false, matrixStackIn, bufferIn, combinedLightIn,
						combinedOverlayIn, mc.getItemRenderer().getModel(stack, null, null, 0));

			}
			matrixStackIn.popPose();
		}
	}

}
