package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class RenderTileDisplayPedestal implements BlockEntityRenderer<DisplayPedestalBlockEntity, RenderTileDisplayPedestal.DisplayPedestalRenderState> {
	private final ItemModelResolver itemModelResolver;

	public RenderTileDisplayPedestal(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public DisplayPedestalRenderState createRenderState() {
		return new DisplayPedestalRenderState();
	}

	@Override
	public void extractRenderState(DisplayPedestalBlockEntity pedestal, DisplayPedestalRenderState state,
			float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(pedestal, state, crumblingOverlay);
		state.items = 0;
		state.rotation = pedestal.getLevel() == null ? 0.0F : pedestal.getLevel().getGameTime() + partialTicks;
		for (int i = 0; i < pedestal.inventory.size() && i < state.itemStates.length; i++) {
			var stack = pedestal.inventory.get(i);
			state.itemStates[i].clear();
			if (!stack.isEmpty()) {
				state.items++;
				this.itemModelResolver.updateForTopItem(state.itemStates[i], stack, ItemDisplayContext.FIXED,
						pedestal.getLevel(), (ItemOwner) null, i);
			}
		}
	}

	@Override
	public void submit(DisplayPedestalRenderState state, PoseStack poseStack, SubmitNodeCollector nodes,
			CameraRenderState cameraState) {
		if (state.items == 0) {
			return;
		}

		float anglePer = 360F / state.items;
		float totalAngle = 0F;
		for (int i = 0; i < state.itemStates.length; i++) {
			ItemStackRenderState item = state.itemStates[i];
			if (item.isEmpty()) {
				continue;
			}

			poseStack.pushPose();
			poseStack.translate(0.5F, 1.55F, 0.5F);
			poseStack.mulPose(Vector3.YP.rotationDegrees((totalAngle += anglePer) + state.rotation).toMoj());
			poseStack.translate(0.025F, -0.5F, 0.025F);
			poseStack.mulPose(Vector3.YP.rotationDegrees(90f).toMoj());
			poseStack.translate(0D, 0.175D + i * 0.25, 0F);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			item.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
	}

	public static class DisplayPedestalRenderState extends BlockEntityRenderState {
		public final ItemStackRenderState[] itemStates = { new ItemStackRenderState() };
		public int items;
		public float rotation;
	}
}
