package com.vincenthuto.hutoslib.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;

import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class RenderItemGuideBook extends BlockEntityWithoutLevelRenderer {
	public BookModel model;
	public static ResourceLocation defaultText = new ResourceLocation(HutosLib.MOD_ID,
			"textures/gui/hl_guide_book_text_default.png");

	public RenderItemGuideBook(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
		super(p_172550_, p_172551_);
		this.model = new BookModel(p_172551_.bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack ms,
			MultiBufferSource buffers, int light, int overlay) {

		if (stack.getItem() instanceof ItemGuideBook) {
			ItemGuideBook item = (ItemGuideBook) stack.getItem();
			ms.pushPose();
			ms.translate(0.5D, 0.50D, 0.5D);
			ms.mulPose(Vector3f.XP.rotationDegrees(45));
			ms.mulPose(Vector3f.ZP.rotationDegrees(15));
			ms.mulPose(Vector3f.YP.rotationDegrees(-60));

			float f = (float) item.ticks + 1;
			ms.translate(0.0D, 0.1F + Mth.sin(f * 0.1F) * 0.01F, 0.0D);

			float f1;
			for (f1 = item.nextPageAngle - item.pageAngle; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
			}

			while (f1 < -(float) Math.PI) {
				f1 += ((float) Math.PI * 2F);
			}
			float f2 = item.pageAngle + f1 ;
			ms.mulPose(Vector3f.YP.rotation(-f2));
			ms.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
			float f3 = Mth.lerp(1, item.oFlip, item.flip);
			float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
			float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
			this.model.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), item.close);

			MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource
					.immediate(Tesselator.getInstance().getBuilder());
			if (item.getTexture() != null) {
				VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(model.renderType(item.getTexture()));
				ms.scale(0.75f, 0.75f, 0.75f);
				if (transform == TransformType.GUI) {
					ms.translate(0.15, 0.03, 0);
					ms.scale(0.8f, 0.8f, 0.8f);
					ms.mulPose(new Quaternion(Vector3f.YP, -125, true));
					ms.mulPose(new Quaternion(Vector3f.XP, 35, true));
					ms.mulPose(new Quaternion(Vector3f.ZP, 45, true));

				}

				model.renderToBuffer(ms, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

				irendertypebuffer$impl.endBatch();
				ms.popPose();
			} else {
				VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(model.renderType(defaultText));
				ms.scale(0.75f, 0.75f, 0.75f);
				if (transform == TransformType.GUI) {
					ms.translate(0.15, 0.03, 0);
					ms.scale(0.8f, 0.8f, 0.8f);
					ms.mulPose(new Quaternion(Vector3f.YP, -125, true));
					ms.mulPose(new Quaternion(Vector3f.XP, 35, true));
					ms.mulPose(new Quaternion(Vector3f.ZP, 45, true));

				}

				model.renderToBuffer(ms, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

				irendertypebuffer$impl.endBatch();
				ms.popPose();
			}

		}
	}

	public BookModel getModel() {
		return model;
	}
}