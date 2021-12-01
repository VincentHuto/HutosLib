package com.vincenthuto.hutoslib.common.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentGlimmer extends Enchantment {
	public EnchantmentGlimmer(Enchantment.Rarity p_45290_, EquipmentSlot... p_45291_) {
		super(p_45290_, HLEnchantInit.ALL, p_45291_);
	}

	public int getMinCost(int p_45294_) {
		return 1;
	}

	public int getMaxCost(int p_45296_) {
		return this.getMinCost(p_45296_) + 40;
	}

	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment p_44690_) {
		return true;
	}
	
	@Override
	public boolean canEnchant(ItemStack p_44689_) {
		return true;
	}
	
	@Override
	public boolean isAllowedOnBooks() {
		return true;
	}
	
	
	
	
}