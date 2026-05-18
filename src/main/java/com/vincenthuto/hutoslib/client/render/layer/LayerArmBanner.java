package com.vincenthuto.hutoslib.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.client.render.item.ArmBannerRenderState;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.registry.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class LayerArmBanner<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

	public static final ResourceLocation fallback = HutosLib.rloc(
			"textures/entity/arm_banner/arm_banner.png");

	@SuppressWarnings("rawtypes")
	private final ModelArmBanner modelPauldron;

	@SuppressWarnings("rawtypes")
	public LayerArmBanner(LivingEntityRenderer<T, M> owner) {
		super(owner);
		modelPauldron = new ModelArmBanner(
				Minecraft.getInstance().getEntityModels().bakeLayer(HutosLibModelLayersInit.arm_banner));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void render(PoseStack matrixStack, MultiBufferSource buffer, int lightness, T ent, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

		if (ent instanceof Player player) {
			Inventory inv = player.getInventory();
			ItemStack chest = inv.getArmor(EquipmentSlot.CHEST.getIndex());
			boolean scaleFlag = chest == ItemStack.EMPTY ? false : true;

			BannerFinder.findBanner(player, true).ifPresent((getter) -> {
				ItemStack banner = getter.getBanner();
				if (banner.getItem() instanceof ItemArmBanner type) {
					matrixStack.pushPose();
					this.translateToBody(matrixStack);
					matrixStack.translate(-0.35, -0.05, 0);
					ResourceLocation texture = type.getTexture() != null ? type.getTexture() : fallback;
					if (scaleFlag) {
						matrixStack.scale(1.2f, 1.2f, 1.25f);
						matrixStack.translate(0.01, 0.0, 0);
						renderColoredCutoutModel(modelPauldron, texture, matrixStack, buffer, lightness, player, -1);
					} else {
						renderColoredCutoutModel(modelPauldron, texture, matrixStack, buffer, lightness, player, -1);
					}
					BannerPatternLayers patterns = banner.getOrDefault(DataComponents.BANNER_PATTERNS,
							BannerPatternLayers.EMPTY);
					DyeColor bannerColor = banner.get(DataComponents.BASE_COLOR);
					ArmBannerRenderState bannerState = ArmBannerRenderState.fromBannerData(bannerColor != null,
							patterns.layers().size());
					matrixStack.pushPose();
					matrixStack.scale(1.0F, -1.0F, -1.0F);
					Material material = bannerState.shouldRenderPlate() ? ModelBakery.SHIELD_BASE
							: ModelBakery.NO_PATTERN_SHIELD;
					if (bannerState.shouldRenderPlate()) {
						matrixStack.mulPose(new Quaternion(Vector3.YN, 90, true).toMoj());
						matrixStack.mulPose(new Quaternion(Vector3.ZP, 180, true).toMoj());
						matrixStack.translate(0, 0.3, -0.55);
						matrixStack.scale(0.5f, 0.5f, 0.5f);
						DyeColor baseColor = Objects.requireNonNullElse(bannerColor, DyeColor.WHITE);
						BannerRenderer.renderPatterns(matrixStack, buffer, lightness, OverlayTexture.NO_OVERLAY,
								this.modelPauldron.plate(), material, false, baseColor, patterns, banner.hasFoil());
					}
					matrixStack.popPose();
					matrixStack.popPose();
				}
			});
		}
	}

	private void translateToBody(PoseStack matrixStack) {
		this.getParentModel().leftArm.translateAndRotate(matrixStack);
	}

}
