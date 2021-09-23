package com.vincenthuto.hutoslib.client.render.item;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

public class RenderItemArmBanner extends BlockEntityWithoutLevelRenderer {

	@SuppressWarnings("rawtypes")
	private final ModelArmBanner modelPauldron;
	public static final ResourceLocation fallback = new ResourceLocation(HutosLib.MOD_ID,
			"textures/entity/arm_banner/iron_arm_banner.png");

	@SuppressWarnings("rawtypes")
	public RenderItemArmBanner(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
		super(p_172550_, p_172551_);
		modelPauldron = new ModelArmBanner(p_172551_.bakeLayer(HutosLibModelLayersInit.arm_banner));

	}

	@SuppressWarnings("unused")
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Item item = stack.getItem();
		if (item instanceof ItemArmBanner type) {
			ResourceLocation texture = type.getTexture() != null ? type.getTexture() : fallback;
			if (p_239207_2_ == TransformType.GUI) {
				matrixStack.scale(0.45f, 0.45f, 0.45f);
				matrixStack.mulPose(new Quaternion(Vector3f.ZP, -73.5f, true));
				matrixStack.translate(0.34, 0, 0);
			}

			if (p_239207_2_ == TransformType.THIRD_PERSON_RIGHT_HAND) {
				matrixStack.scale(0.1f, 0.1f, 0.1f);
				matrixStack.mulPose(new Quaternion(Vector3f.ZP, -73.5f, true));
				matrixStack.translate(-0.1, 2.5, 0);
			}
			if (p_239207_2_ == TransformType.THIRD_PERSON_LEFT_HAND) {
				matrixStack.scale(0.1f, 0.1f, 0.1f);
				matrixStack.mulPose(new Quaternion(Vector3f.ZP, -73.5f, true));
				matrixStack.translate(-1.3, -1.75, 0);
			}
			if (p_239207_2_ == TransformType.FIRST_PERSON_RIGHT_HAND) {
				matrixStack.scale(0.2f, 0.2f, 0.2f);
				matrixStack.mulPose(new Quaternion(Vector3f.ZP, -73.5f, true));
				matrixStack.translate(-1.3, 0.75, 0);

			}
			if (p_239207_2_ == TransformType.FIRST_PERSON_LEFT_HAND) {
				matrixStack.scale(0.2f, 0.2f, 0.2f);
				matrixStack.mulPose(new Quaternion(Vector3f.ZP, -73.5f, true));

				matrixStack.translate(-1.3, 0.75, 0);

			}
			matrixStack.pushPose();
			MultiBufferSource.BufferSource impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			VertexConsumer vb = impl.getBuffer(modelPauldron.renderType(texture));
			matrixStack.scale(4.1f, 5f, 4.1f);
			matrixStack.translate(-0.21, 0.02, -0.53);
			matrixStack.mulPose(new Quaternion(Vector3f.ZP, -105f, true));
			matrixStack.mulPose(new Quaternion(Vector3f.YP, -90, true));
			modelPauldron.renderToBuffer(matrixStack, vb, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
					1.0F);
			impl.endBatch();
			matrixStack.popPose();

			boolean flag = stack.getTagElement("BlockEntityTag") != null;
			matrixStack.pushPose();
			matrixStack.scale(1.0F, -1.0F, -1.0F);

			Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
			VertexConsumer vertexconsumer = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(buffer,
					this.modelPauldron.renderType(material.atlasLocation()), true, stack.hasFoil()));
			if (flag) {
				matrixStack.translate(0, 0.05, -0.25);
				matrixStack.mulPose(new Quaternion(Vector3f.ZN, 75, true));
				matrixStack.scale(1.7f, 1.7f, 1.7f);
				List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity
						.createPatterns(ItemArmBanner.getColor(stack), BannerBlockEntity.getItemPatterns(stack));
				BannerRenderer.renderPatterns(matrixStack, buffer, combinedLight, combinedOverlay,
						this.modelPauldron.plate(), material, false, list, stack.hasFoil());
			} else {
				// If there is no banner dont render shit

			}

			matrixStack.popPose();
		}
	}

	public static void render_plate(PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay,
			ModelPart parts, Material material, boolean p_241717_6_, List<Pair<BannerPattern, DyeColor>> list,
			boolean hasEffect) {
	}
}