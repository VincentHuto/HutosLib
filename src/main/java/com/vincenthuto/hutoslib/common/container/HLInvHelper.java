package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.common.block.entity.TileSimpleInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class HLInvHelper {

	// Combined a few methods into one more generic one
	public static ItemStack findItemInPlayerInv(Player player, Class<? extends Item> item) {
		if (item.isInstance(player.getOffhandItem().getItem()))
			return player.getMainHandItem();
		if (item.isInstance(player.getMainHandItem().getItem()))
			return player.getOffhandItem();
		Inventory inventory = player.getInventory();
		for (int i = 0; i <= 35; i++) {
			ItemStack stack = inventory.getItem(i);
			if (item.isInstance(stack.getItem()))
				return stack;
		}
		return ItemStack.EMPTY;
	}

	@Nullable
	public static IItemHandler getInventory(Level world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);

		if (te == null) {
			return null;
		}

		LazyOptional<IItemHandler> ret = te.getCapability(ForgeCapabilities.ITEM_HANDLER, side);
		if (!ret.isPresent()) {
			ret = te.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
		}
		return ret.orElse(null);
	}

	public static void withdrawFromInventory(TileSimpleInventory inv, Player player) {
		for (int i = inv.inventorySize() - 1; i >= 0; i--) {
			ItemStack stackAt = inv.getItemHandler().getItem(i);
			if (!stackAt.isEmpty()) {
				ItemStack copy = stackAt.copy();
				player.getInventory().placeItemBackInInventory(copy);
				inv.getItemHandler().setItem(i, ItemStack.EMPTY);
				break;
			}
		}
	}

}
