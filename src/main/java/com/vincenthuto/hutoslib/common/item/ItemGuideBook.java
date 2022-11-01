package com.vincenthuto.hutoslib.common.item;

import java.util.Random;
import java.util.function.Consumer;

import com.vincenthuto.hutoslib.client.render.item.RenderItemGuideBook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ItemGuideBook extends Item {
	// Essentially taken from the enchanting table animation code with some slight
	// modifications
	public int ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float nextPageAngle;
	public float pageAngle;
	public float tRot;
	public float close;
	private static final Random random = new Random();
	private ResourceLocation texture;

	public ItemGuideBook(Properties prop, ResourceLocation loc) {
		super(prop);
		this.texture = loc;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (entityIn instanceof Player player) {
			ItemStack oheld = player.getOffhandItem();
			boolean offHand = stack.getItem() == oheld.getItem();

			ItemStack mheld = player.getMainHandItem();
			boolean mainHand = stack.getItem() == mheld.getItem();

			if (mainHand || offHand) {
				this.pageTurningSpeed = this.nextPageTurningSpeed;
				this.pageAngle = this.nextPageAngle;
				this.nextPageTurningSpeed += 0.1F;
				if (this.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
					float f1 = this.flipT;
					do {
						this.flipT += random.nextInt(4) - random.nextInt(4);
					} while (f1 == this.flipT);
				}
				while (this.nextPageAngle >= (float) Math.PI) {
					this.nextPageAngle -= ((float) Math.PI * 2F);
				}
				while (this.nextPageAngle < -(float) Math.PI) {
					this.nextPageAngle += ((float) Math.PI * 2F);
				}
				while (this.tRot >= (float) Math.PI) {
					this.tRot -= ((float) Math.PI * 2F);
				}
				while (this.tRot < -(float) Math.PI) {
					this.tRot += ((float) Math.PI * 2F);
				}
				float f2;
				for (f2 = this.tRot - this.nextPageAngle; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
				}

				while (f2 < -(float) Math.PI) {
					f2 += ((float) Math.PI * 2F);
				}
				this.nextPageAngle += f2 * 0.4F;
				this.nextPageTurningSpeed = Mth.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
				this.oFlip = this.flip;
				float f = (this.flipT - this.flip) * 0.4F;
				f = Mth.clamp(f, -0.2F, 0.2F);
				this.flipA += (f - this.flipA) * 0.9F;
				this.flip += this.flipA;
				if (close < 1f) {
					close += 0.015f;
				}
			} else {
				if (close > 0f) {
					close -= 0.015f;
				}
			}
		}

	}

	@Override
		public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(RenderPropTome.INSTANCE);
	}
}

class RenderPropTome implements IClientItemExtensions {

	public static RenderPropTome INSTANCE = new RenderPropTome();


	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return new RenderItemGuideBook(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
				Minecraft.getInstance().getEntityModels());
	}
}
