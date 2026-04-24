package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.registry.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/**
 * Custom item renderer for arm banners.
 * NOTE: BlockEntityWithoutLevelRenderer is removed in 1.21.11.
 * Register this via SpecialModelRenderer or a client extension in your mod setup.
 */
public class RenderItemArmBanner {

	public static final Identifier fallback = HutosLib.rloc(
			"textures/entity/arm_banner/iron_arm_banner.png");

	@SuppressWarnings("rawtypes")
	private final ModelArmBanner modelPauldron;

	@SuppressWarnings("rawtypes")
	public RenderItemArmBanner() {
		modelPauldron = new ModelArmBanner(
				Minecraft.getInstance().getEntityModels().bakeLayer(HutosLibModelLayersInit.arm_banner));
	}

	@SuppressWarnings("unused")
	public void renderByItem(ItemStack stack, ItemDisplayContext p_239207_2_, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Item item = stack.getItem();
		if (item instanceof ItemArmBanner type) {
			Identifier texture = type.getTexture() != null ? type.getTexture() : fallback;
			if (p_239207_2_ == ItemDisplayContext.GUI) {
				matrixStack.scale(0.45f, 0.45f, 0.45f);
				matrixStack.mulPose(new Quaternion(Vector3.ZP, -73.5f, true).toMoj());
				matrixStack.translate(0.34, 0, 0);
			}
			if (p_239207_2_ == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
				matrixStack.scale(0.1f, 0.1f, 0.1f);
				matrixStack.mulPose(new Quaternion(Vector3.ZP, -73.5f, true).toMoj());
				matrixStack.translate(-0.1, 2.5, 0);
			}
			if (p_239207_2_ == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
				matrixStack.scale(0.1f, 0.1f, 0.1f);
				matrixStack.mulPose(new Quaternion(Vector3.ZP, -73.5f, true).toMoj());
				matrixStack.translate(-1.3, -1.75, 0);
			}
			if (p_239207_2_ == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
				matrixStack.scale(0.2f, 0.2f, 0.2f);
				matrixStack.mulPose(new Quaternion(Vector3.ZP, -73.5f, true).toMoj());
				matrixStack.translate(-1.3, 0.75, 0);
			}
			if (p_239207_2_ == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
				matrixStack.scale(0.2f, 0.2f, 0.2f);
				matrixStack.mulPose(new Quaternion(Vector3.ZP, -73.5f, true).toMoj());
				matrixStack.translate(-1.3, 0.75, 0);
			}

			matrixStack.pushPose();
			VertexConsumer vb = buffer.getBuffer(modelPauldron.renderType(texture));
			matrixStack.scale(4.1f, 5f, 4.1f);
			matrixStack.translate(-0.21, 0.02, -0.53);
			matrixStack.mulPose(new Quaternion(Vector3.ZP, -105f, true).toMoj());
			matrixStack.mulPose(new Quaternion(Vector3.YP, -90, true).toMoj());
			modelPauldron.renderToBuffer(matrixStack, vb, combinedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
			matrixStack.popPose();

			// TODO: Banner pattern rendering requires MaterialSet (SubmitNodeCollector-based API).
			// BannerRenderer.submitPatterns is no longer MultiBufferSource-compatible.
			// Re-implement via a SpecialModelRenderer that has access to MaterialSet.
		}
	}
}
