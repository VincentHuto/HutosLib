package com.vincenthuto.hutoslib.client.render.layer;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

public class LayerArmBanner<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

	public static final ResourceLocation fallback = new ResourceLocation(HutosLib.MOD_ID,
			"textures/entity/arm_banner/arm_banner.png");
	public static final Material LOCATION_ROYAL_GUARD_SHIELD_BASE = new Material(TextureAtlas.LOCATION_BLOCKS,
			new ResourceLocation(HutosLib.MOD_ID, "entity/royal_guard_shield_base"));

	@SuppressWarnings("rawtypes")
	private final ModelArmBanner modelPauldron;

	@SuppressWarnings("rawtypes")
	public LayerArmBanner(LivingEntityRenderer<T, M> owner) {
		super(owner);
		modelPauldron = new ModelArmBanner(
				Minecraft.getInstance().getEntityModels().bakeLayer(HutosLibModelLayersInit.arm_banner));
	}

	private void translateToBody(PoseStack matrixStack) {
		this.getParentModel().leftArm.translateAndRotate(matrixStack);
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
				if (banner.getItem()instanceof ItemArmBanner type) {
					matrixStack.pushPose();
					this.translateToBody(matrixStack);
					matrixStack.translate(-0.35, -0.05, 0);
					ResourceLocation texture = type.getTexture() != null ? type.getTexture() : fallback;
					if (scaleFlag) {
						matrixStack.scale(1.2f, 1.2f, 1.25f);
						matrixStack.translate(0.01, 0.0, 0);
						renderColoredCutoutModel(modelPauldron, texture, matrixStack, buffer, lightness, player, 1.0f,
								1.0f, 1.0f);
					} else {
						renderColoredCutoutModel(modelPauldron, texture, matrixStack, buffer, lightness, player, 1.0f,
								1.0f, 1.0f);

					}
					boolean flag = banner.getTagElement("BlockEntityTag") != null;
					matrixStack.pushPose();
					matrixStack.scale(1.0F, -1.0F, -1.0F);
					Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
					VertexConsumer vertexconsumer = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(buffer,
							this.modelPauldron.renderType(material.atlasLocation()), true, banner.hasFoil()));
					if (flag) {
						List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.createPatterns(
								ItemArmBanner.getColor(banner), BannerBlockEntity.getItemPatterns(banner));
						matrixStack.mulPose(new Quaternion(Vector3f.YN, 90, true));
						matrixStack.mulPose(new Quaternion(Vector3f.ZP, 180, true));
						matrixStack.translate(0, 0.3, -0.55);
						matrixStack.scale(0.5f, 0.5f, 0.5f);
						BannerRenderer.renderPatterns(matrixStack, buffer, lightness, OverlayTexture.NO_OVERLAY,
								this.modelPauldron.plate(), material, false, list, banner.hasFoil());
					}
					matrixStack.popPose();
					matrixStack.popPose();
				}
			});
		}
	}

}
