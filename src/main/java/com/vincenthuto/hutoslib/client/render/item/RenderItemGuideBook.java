package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import com.vincenthuto.hutoslib.math.Quaternion;
import com.vincenthuto.hutoslib.math.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Custom item renderer for the guide book.
 * NOTE: BlockEntityWithoutLevelRenderer is removed in 1.21.11.
 * Register this via SpecialModelRenderer or a client extension in your mod setup.
 */
public class RenderItemGuideBook {
	public static Identifier defaultText = HutosLib.rloc(
			"textures/gui/hl_guide_book_text_default.png");
	public BookModel model;

	public RenderItemGuideBook() {
		this.model = new BookModel(
				Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
	}

	public BookModel getModel() {
		return model;
	}

	public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack ms,
			MultiBufferSource buffers, int light, int overlay) {

		if (transform == ItemDisplayContext.GUI) {
			ms.mulPose(Vector3.XP.rotationDegrees(80).toMoj());
			ms.mulPose(Vector3.YN.rotationDegrees(30).toMoj());
			ms.mulPose(Vector3.ZN.rotationDegrees(-20).toMoj());
			ms.translate(-1.5D, -0.45D, -0.075D);
		}
		if (stack.getItem() instanceof ItemGuideBook item) {
			ms.pushPose();
			ms.translate(0.5D, 0.50D, 0.5D);
			ms.mulPose(Vector3.XP.rotationDegrees(45).toMoj());
			ms.mulPose(Vector3.ZP.rotationDegrees(15).toMoj());
			ms.mulPose(Vector3.YP.rotationDegrees(-60).toMoj());

			float f = (float) item.ticks + 1;
			ms.translate(0.0D, 0.1F + Mth.sin(f * 0.1F) * 0.01F, 0.0D);

			float f1;
			for (f1 = item.nextPageAngle - item.pageAngle; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
			}
			while (f1 < -(float) Math.PI) {
				f1 += ((float) Math.PI * 2F);
			}
			float f2 = item.pageAngle + f1;
			ms.mulPose(Vector3.YP.rotation(-f2).toMoj());
			ms.mulPose(Vector3.ZP.rotationDegrees(80.0F).toMoj());
			float f3 = Mth.lerp(1, item.oFlip, item.flip);
			float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
			float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;

			// 1.21.11: BookModel.setupAnim now takes a BookModel.State record
			this.model.setupAnim(new BookModel.State(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), item.close));

			Identifier bookTexture = item.getTexture() != null ? item.getTexture() : defaultText;
			if (item.getTexture() != null) {
				VertexConsumer ivertexbuilder = buffers.getBuffer(model.renderType(bookTexture));
				ms.scale(0.75f, 0.75f, 0.75f);
				if (transform == ItemDisplayContext.GUI) {
					ms.translate(0.15, 0.03, 0);
					ms.scale(0.8f, 0.8f, 0.8f);
					ms.mulPose(new Quaternion(Vector3.XP, 35, true).toMoj());
					ms.mulPose(new Quaternion(Vector3.ZP, 45, true).toMoj());
				}
				model.renderToBuffer(ms, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
				ms.popPose();
			} else {
				VertexConsumer ivertexbuilder = buffers.getBuffer(model.renderType(defaultText));
				ms.scale(0.75f, 0.75f, 0.75f);
				if (transform == ItemDisplayContext.GUI) {
					ms.translate(0.15, 0.03, 0);
					ms.scale(0.8f, 0.8f, 0.8f);
					ms.mulPose(new Quaternion(Vector3.YP, -125, true).toMoj());
					ms.mulPose(new Quaternion(Vector3.XP, 35, true).toMoj());
					ms.mulPose(new Quaternion(Vector3.ZP, 45, true).toMoj());
				}
				model.renderToBuffer(ms, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
				ms.popPose();
			}
		}
	}
}

