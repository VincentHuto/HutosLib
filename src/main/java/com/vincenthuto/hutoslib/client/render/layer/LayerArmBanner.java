package com.vincenthuto.hutoslib.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.registry.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;

public class LayerArmBanner<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
	public static final ContextKey<ItemStack> ARM_BANNER_STACK = new ContextKey<>(HutosLib.rloc("arm_banner_stack"));
	public static final Identifier FALLBACK = HutosLib.rloc("textures/entity/arm_banner/arm_banner.png");

	private final ModelArmBanner modelPauldron;

	public LayerArmBanner(RenderLayerParent<S, M> owner) {
		super(owner);
		this.modelPauldron = new ModelArmBanner(
				Minecraft.getInstance().getEntityModels().bakeLayer(HutosLibModelLayersInit.arm_banner));
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodes, int light, S renderState, float yRot, float xRot) {
		ItemStack banner = ((IRenderStateExtension) renderState).getRenderDataOrDefault(ARM_BANNER_STACK, ItemStack.EMPTY);
		if (!(banner.getItem() instanceof ItemArmBanner type)) {
			return;
		}

		poseStack.pushPose();
		this.getParentModel().leftArm.translateAndRotate(poseStack);
		poseStack.translate(-0.35, -0.05, 0);
		if (!renderState.chestEquipment.isEmpty()) {
			poseStack.scale(1.2f, 1.2f, 1.25f);
			poseStack.translate(0.01, 0.0, 0);
		}

		Identifier texture = type.getTexture() != null ? type.getTexture() : FALLBACK;
		int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
		nodes.submitModel(this.modelPauldron, ModelArmBanner.State.SHOULDER, poseStack, texture, light, overlay,
				-1, null);

		BannerPatternLayers patterns = banner.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
		if (!patterns.layers().isEmpty()) {
			poseStack.pushPose();
			poseStack.scale(1.0F, -1.0F, -1.0F);
			poseStack.mulPose(new Quaternion(Vector3.YN, 90, true).toMoj());
			poseStack.mulPose(new Quaternion(Vector3.ZP, 180, true).toMoj());
			poseStack.translate(0, 0.3, -0.55);
			poseStack.scale(0.5f, 0.5f, 0.5f);
			BannerRenderer.submitPatterns(Minecraft.getInstance().getAtlasManager(), poseStack, nodes, light,
					overlay, this.modelPauldron, ModelArmBanner.State.PLATE, false,
					banner.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE), patterns, null);
			poseStack.popPose();
		}

		poseStack.popPose();
	}
}
