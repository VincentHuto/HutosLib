package com.vincenthuto.hutoslib.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.block.DictationTableBlock;
import com.vincenthuto.hutoslib.common.book.FieldNotes;
import com.vincenthuto.hutoslib.common.block.entity.DictationTableBlockEntity;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class DictationTableRenderer implements BlockEntityRenderer<DictationTableBlockEntity> {
	private static final ResourceLocation FALLBACK_TEXTURE = HutosLib.rloc("textures/gui/guide/hl_guide_model.png");

	private final BookModel bookModel;

	public DictationTableRenderer(BlockEntityRendererProvider.Context context) {
		this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
	}

	@Override
	public void render(DictationTableBlockEntity table, float partialTicks, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		ItemStack liber = table.getBook();
		Direction facing = table.getBlockState().getValue(DictationTableBlock.FACING);
        if (!(liber.getItem() instanceof ItemGuideBook item)) return;
        if (!(table.getBlockState().getBlock() instanceof DictationTableBlock block)) return;
        boolean pending = block.supportsDictation() && block.pendingGlowColor() != 0
                && FieldNotes.hasPending(Minecraft.getInstance().player, liber, block);
		ResourceLocation texture = item.getTexture() != null ? item.getTexture() : FALLBACK_TEXTURE;
		poseStack.pushPose();
		poseStack.translate(0.5D, block.bookHeight(), 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(rotationForFacing(facing) + block.bookRotationOffset()));
		poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
		poseStack.scale(1, 1F, 1F);
        bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.0F);
		VertexConsumer vertexConsumer = bufferSource.getBuffer(bookModel.renderType(texture));
		bookModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        if (pending) {
            float time = (table.getLevel() == null ? 0 : table.getLevel().getGameTime()) + partialTicks;
            int alpha = Math.round(35 + 25 * (0.5F + 0.5F * Mth.sin(time * 0.08F)));
            var glow = bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.entityTranslucentEmissive(texture));
            bookModel.renderToBuffer(poseStack, glow, 0xF000F0, OverlayTexture.NO_OVERLAY, (alpha << 24) | block.pendingGlowColor());
        }
		poseStack.popPose();
	}

	private static float rotationForFacing(Direction facing) {
		return switch (facing) {
		case NORTH -> 180.0F;
		case EAST -> 90.F;
		case WEST -> -90.0F;
		default -> 0.0F;
		};
	}
}
