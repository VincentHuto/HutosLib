package com.vincenthuto.hutoslib.common.item;

import com.google.common.collect.Sets;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemKnapper extends Item {
	public static final ItemAbility KNAPPER_DIG = ItemAbility.get("knapper_dig");
	public static final Set<ItemAbility> DEFAULT_KNAPPER_ACTIONS = Stream.of(KNAPPER_DIG)
			.collect(Collectors.toCollection(Sets::newIdentityHashSet));
	public static TagKey<Block> EFFECTIVE_ON = TagKey.create(Registries.BLOCK,
			Identifier.fromNamespaceAndPath("minecraft", "mineable/knapper"));

	private final float speed;

	public ItemKnapper(float speedIn, float attackDamageIn, float attackSpeedIn, HLModTiers tier, Properties builderIn) {
		super(tier.material().applyToolProperties(builderIn, EFFECTIVE_ON, attackDamageIn, attackSpeedIn, 0.0F));
		this.speed = speedIn;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return state.is(EFFECTIVE_ON) ? speed : super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (!worldIn.isClientSide() && state.is(EFFECTIVE_ON)) {
			ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					new ItemStack(HLItemInit.obsidian_flakes.get(), worldIn.getRandom().nextInt(3)));
			worldIn.addFreshEntity(ent);
		}
		return super.mineBlock(stack, worldIn, state, pos, entityLiving);
	}
}
