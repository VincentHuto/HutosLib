package com.hutoslib.common.item;

import java.util.Set;

import com.google.common.collect.Sets;
import com.hutoslib.common.block.HutosLibBlockMaterials;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKnapper extends ToolItem {
	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN);
	private float speed;

	public ItemKnapper(float speedIn, float attackDamageIn, float attackSpeedIn, IItemTier tier, Properties builderIn) {
		super(attackDamageIn, -2.8f, tier, EFFECTIVE_ON, builderIn);
		this.speed = speedIn;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
		return EFFECTIVE_ON.contains(blockIn.getBlock()) || blockIn.getMaterial() == HutosLibBlockMaterials.OBSIDIAN;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return EFFECTIVE_ON.contains(state.getBlock()) ? speed : 0.5f;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (canHarvestBlock(state)) {
			ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					new ItemStack(HutosLibItemInit.obsidian_flakes.get(), worldIn.rand.nextInt(3)));
			worldIn.addEntity(ent);
		}

		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}
}
