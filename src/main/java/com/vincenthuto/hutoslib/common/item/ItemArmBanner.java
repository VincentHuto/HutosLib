package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

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
	public Component getName(ItemStack stack) {
		DyeColor color = stack.get(DataComponents.BASE_COLOR);
		if (color != null) {
			return Component.translatable(this.getDescriptionId() + "." + color.getName());
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
			Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
		DyeColor color = stack.get(DataComponents.BASE_COLOR);
		if (color != null) {
			tooltipAdder.accept(Component.translatable("tooltip.hutoslib.arm_banner.base_color",
					Component.translatable("color.minecraft." + color.getName())).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return InteractionResult.CONSUME;
	}
}
