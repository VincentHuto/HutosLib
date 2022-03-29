package com.vincenthuto.hutoslib.common.item;

import java.util.function.Supplier;

import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("deprecation")
public enum HLModTiers implements Tier {

	WOOD(3, 59, 2.0F, 0.0F, 15, () -> {
		return Ingredient.of(ItemTags.PLANKS);
	}), STONE(3, 131, 4.0F, 1.0F, 5, () -> {
		return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
	}), IRON(3, 250, 6.0F, 2.0F, 14, () -> {
		return Ingredient.of(Items.IRON_INGOT);
	}), DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
		return Ingredient.of(Items.DIAMOND);
	}), GOLD(3, 32, 12.0F, 0.0F, 22, () -> {
		return Ingredient.of(Items.GOLD_INGOT);
	}), NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> {
		return Ingredient.of(Items.NETHERITE_INGOT);
	});

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final LazyLoadedValue<Ingredient> repairMaterial;

	private HLModTiers(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn,
			int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
		this.harvestLevel = harvestLevelIn;
		this.maxUses = maxUsesIn;
		this.efficiency = efficiencyIn;
		this.attackDamage = attackDamageIn;
		this.enchantability = enchantabilityIn;
		this.repairMaterial = new LazyLoadedValue<>(repairMaterialIn);
	}

	public int getUses() {
		return this.maxUses;
	}

	public float getSpeed() {
		return this.efficiency;
	}

	public float getAttackDamageBonus() {
		return this.attackDamage;
	}

	public int getLevel() {
		return this.harvestLevel;
	}

	public int getEnchantmentValue() {
		return this.enchantability;
	}

	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}
}