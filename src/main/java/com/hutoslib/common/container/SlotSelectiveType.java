package com.hutoslib.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotSelectiveType extends Slot {
	int limit;
	Class<? extends Item> itemType;

	public SlotSelectiveType(IInventory iItemHandlerModifiable, Class<? extends Item> itemType, int index, int limit,
			int xPosition, int yPosition) {
		super(iItemHandlerModifiable, index, xPosition, yPosition);
		this.itemType = itemType;
		this.limit = limit;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return itemType.isInstance(stack.getItem());
	}

	@Override
	public int getSlotStackLimit() {
		return limit;
	}

}
