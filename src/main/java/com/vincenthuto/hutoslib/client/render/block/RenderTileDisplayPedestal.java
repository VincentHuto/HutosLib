package com.vincenthuto.hutoslib.client.render.block;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderTileDisplayPedestal
		implements BlockEntityRenderer<DisplayPedestalBlockEntity, RenderTileDisplayPedestal.DisplayPedestalRenderState> {

	private final ItemRenderer itemRenderer;

	public RenderTileDisplayPedestal(BlockEntityRendererProvider.Context pContext) {
		this.itemRenderer = pContext.getItemRenderer();
	}

	public static class DisplayPedestalRenderState extends BlockEntityRenderState {
		final List<ItemStack> inventorySnapshot = new ArrayList<>();
		long gameTime;
	}

	@Override
	public DisplayPedestalRenderState createRenderState() {
		return new DisplayPedestalRenderState();
	}

	@Override
	public void extractRenderState(DisplayPedestalBlockEntity te, DisplayPedestalRenderState state,
			float partialTick) {
		state.inventorySnapshot.clear();
		for (int i = 0; i < te.inventory.size(); i++) {
			state.inventorySnapshot.add(te.inventory.get(i).copy());
		}
		state.gameTime = te.getLevel() != null ? te.getLevel().getGameTime() : 0L;
	}

	@Override
	public void submit(DisplayPedestalRenderState state, PoseStack poseStack,
			SubmitNodeCollector collector, int packedOverlay, Camera camera) {
		List<ItemStack> inventory = state.inventorySnapshot;
		int items = 0;
		for (int i = 0; i < inventory.size(); i++)
			if (inventory.get(i).isEmpty())
				break;
			else
				items++;

		float[] angles = new float[inventory.size()];
		float anglePer = 360F / (items == 0 ? 1 : items);
		float totalAngle = 0F;
		for (int i = 0; i < angles.length; i++)
			angles[i] = totalAngle += anglePer;

		for (int i = 0; i < inventory.size(); i++) {
			poseStack.pushPose();
			poseStack.translate(0.5F, 1.55F, 0.5F);
			poseStack.mulPose(Vector3.YP.rotationDegrees(angles[i] + state.gameTime).toMoj());
			poseStack.translate(0.025F, -0.5F, 0.025F);
			poseStack.mulPose(Vector3.YP.rotationDegrees(90f).toMoj());
			poseStack.translate(0D, 0.175D + i * 0.25, 0F);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			ItemStack stack = inventory.get(i);
			if (!stack.isEmpty()) {
				this.itemRenderer.renderStatic(null, stack, ItemDisplayContext.FIXED, true, poseStack, collector, null,
						camera.packedLight(), packedOverlay, i);
			}
			poseStack.popPose();
		}
	}
}
