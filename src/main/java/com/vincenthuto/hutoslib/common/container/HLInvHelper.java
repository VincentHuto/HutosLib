package com.vincenthuto.hutoslib.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class HLInvHelper {
	public static IItemHandler getInventory(Level world, BlockPos pos, Direction side) {
		// TODO 26.1: migrate callers to the new ResourceHandler item transfer capability.
		return null;
	}

	public static void withdrawFromInventory(BlockEntity blockEntity, Player player) {
		if (blockEntity instanceof com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity pedestal
				&& !pedestal.inventory.isEmpty()) {
			ItemStack stack = pedestal.removeItemNoUpdate(0);
			if (!stack.isEmpty()) {
				player.addItem(stack);
				blockEntity.setChanged();
			}
		}
	}
}
