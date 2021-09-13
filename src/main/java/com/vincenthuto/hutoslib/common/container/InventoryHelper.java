package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.common.block.entity.TileSimpleInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class InventoryHelper {

	@Nullable
	public static IItemHandler getInventory(Level world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);

		if (te == null) {
			return null;
		}

		LazyOptional<IItemHandler> ret = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		if (!ret.isPresent()) {
			ret = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		}
		return ret.orElse(null);
	}

	public static void withdrawFromInventory(TileSimpleInventory inv, Player player) {
		for (int i = inv.getSizeInventory() - 1; i >= 0; i--) {
			ItemStack stackAt = inv.getItemHandler().getStackInSlot(i);
			if (!stackAt.isEmpty()) {
				ItemStack copy = stackAt.copy();
				player.getInventory().placeItemBackInInventory(copy);
				inv.getItemHandler().setStackInSlot(i, ItemStack.EMPTY);
				break;
			}
		}
	}
}
