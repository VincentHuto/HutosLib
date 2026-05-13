package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.level.Level;

public class ItemArmBanner extends Item implements IBannerSlotItem {
	public ArmorMaterial material;
	Identifier modellocation;

	public static DyeColor getColor(ItemStack stack) {
		DyeColor color = stack.get(DataComponents.BASE_COLOR);
		return color != null ? color : DyeColor.WHITE;
	}

	public ItemArmBanner(Properties prop, ArmorMaterial materialIn, Identifier modellocation) {
		super(prop.stacksTo(1));
		this.material = materialIn;
		this.modellocation = modellocation;
	}

	public Identifier getTexture() {
		return modellocation;
	}

	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return InteractionResult.CONSUME;
	}
}
