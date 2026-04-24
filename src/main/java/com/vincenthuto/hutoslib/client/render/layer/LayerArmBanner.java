package com.vincenthuto.hutoslib.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.registry.HutosLibModelLayersInit;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LayerArmBanner<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {

	public static final Identifier fallback = HutosLib.rloc(
			"textures/entity/arm_banner/arm_banner.png");

	@SuppressWarnings("rawtypes")
	private final ModelArmBanner modelPauldron;

	@SuppressWarnings("rawtypes")
	public LayerArmBanner(RenderLayerParent<S, M> owner) {
		super(owner);
		modelPauldron = new ModelArmBanner(
				Minecraft.getInstance().getEntityModels().bakeLayer(HutosLibModelLayersInit.arm_banner));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void submit(PoseStack matrixStack, SubmitNodeCollector collector, int lightness, S state,
			float limbSwing, float limbSwingAmount) {

		// Use chestEquipment from render state to determine if wearing chest armor
		boolean scaleFlag = !state.chestEquipment.isEmpty();

		// NOTE: BannerFinder.findBanner() requires a Player entity which is not
		// available in the render state. This only works for the local player.
		// For full multi-player support, move banner data into a custom render state.
		Player player = Minecraft.getInstance().player;
		if (player == null) return;

		BannerFinder.findBanner(player, true).ifPresent((getter) -> {
			ItemStack banner = getter.getBanner();
			if (banner.getItem() instanceof ItemArmBanner type) {
				matrixStack.pushPose();
				this.translateToBody(matrixStack);
				matrixStack.translate(-0.35, -0.05, 0);
				Identifier texture = type.getTexture() != null ? type.getTexture() : fallback;
				if (scaleFlag) {
					matrixStack.scale(1.2f, 1.2f, 1.25f);
					matrixStack.translate(0.01, 0.0, 0);
					renderColoredCutoutModel(modelPauldron, texture, matrixStack, collector, lightness, state,
							-1, 0);
				} else {
					renderColoredCutoutModel(modelPauldron, texture, matrixStack, collector, lightness, state,
							-1, 0);
				}
				// TODO: Banner pattern rendering requires MaterialSet from a BakingContext/BER
				// context. To re-enable patterns, inject MaterialSet via the renderer setup
				// and call BannerRenderer.submitPatterns(...) with the correct Model<S>
				// wrapping modelPauldron.plate().
				matrixStack.popPose();
			}
		});
	}

	private void translateToBody(PoseStack matrixStack) {
		this.getParentModel().leftArm.translateAndRotate(matrixStack);
	}
}
