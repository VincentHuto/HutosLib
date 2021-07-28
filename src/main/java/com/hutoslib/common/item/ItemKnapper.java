package com.hutoslib.common.item;

import com.hutoslib.common.block.HutosLibBlockMaterials;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemKnapper extends DiggerItem {
	private static final Tag<Block> EFFECTIVE_ON = HutosLibBlockMaterials.knapper;
	private float speed;

	public ItemKnapper(float speedIn, float attackDamageIn, float attackSpeedIn, Tier tier, Properties builderIn) {
		super(attackDamageIn, -2.8f, tier, EFFECTIVE_ON, builderIn);
		this.speed = speedIn;
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, BlockState state) {
		// TODO Auto-generated method stub
		return EFFECTIVE_ON.contains(state.getBlock()) || state.getMaterial() == HutosLibBlockMaterials.OBSIDIAN;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return EFFECTIVE_ON.contains(state.getBlock()) ? speed : 0.5f;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (canHarvestBlock(stack, state)) {
			ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					new ItemStack(HutosLibItemInit.obsidian_flakes.get(), worldIn.random.nextInt(3)));
			worldIn.addFreshEntity(ent);
		}

		return super.mineBlock(stack, worldIn, state, pos, entityLiving);
	}
}
