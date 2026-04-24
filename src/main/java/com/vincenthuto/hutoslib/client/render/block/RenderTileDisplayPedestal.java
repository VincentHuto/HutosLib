package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RenderTileDisplayPedestal
		implements BlockEntityRenderer<DisplayPedestalBlockEntity, RenderTileDisplayPedestal.DisplayPedestalRenderState> {

	private final ItemModelResolver itemModelResolver;

	public RenderTileDisplayPedestal(BlockEntityRendererProvider.Context pContext) {
		this.itemModelResolver = pContext.itemModelResolver();
	}

	public static class DisplayPedestalRenderState extends BlockEntityRenderState {
		final List<ItemStackRenderState> inventorySnapshot = new ArrayList<>();
		long gameTime;
	}

	@Override
	public DisplayPedestalRenderState createRenderState() {
		return new DisplayPedestalRenderState();
	}

	@Override
	public void extractRenderState(DisplayPedestalBlockEntity te, DisplayPedestalRenderState state,
			float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTick, cameraPos, crumblingOverlay);
		state.inventorySnapshot.clear();
		int seed = (int) te.getBlockPos().asLong();
		for (int i = 0; i < te.inventory.size(); i++) {
			ItemStack stack = te.inventory.get(i);
			if (!stack.isEmpty()) {
				ItemStackRenderState itemState = new ItemStackRenderState();
				this.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, te.getLevel(), null, seed + i);
				state.inventorySnapshot.add(itemState);
			}
		}
		state.gameTime = te.getLevel() != null ? te.getLevel().getGameTime() : 0L;
	}

	@Override
	public void submit(DisplayPedestalRenderState state, PoseStack poseStack,
			SubmitNodeCollector collector, CameraRenderState cameraState) {
		List<ItemStackRenderState> inventory = state.inventorySnapshot;
		if (inventory.isEmpty()) {
			return;
		}

		float anglePer = 360F / inventory.size();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStackRenderState itemState = inventory.get(i);
			if (itemState.isEmpty()) {
				continue;
			}

			poseStack.pushPose();
			poseStack.translate(0.5F, 1.55F, 0.5F);
			poseStack.mulPose(Axis.YP.rotationDegrees(anglePer * i + state.gameTime));
			poseStack.translate(0.025F, -0.5F, 0.025F);
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
			poseStack.translate(0D, 0.175D + i * 0.25, 0F);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, i);
			poseStack.popPose();
		}
	}
}
