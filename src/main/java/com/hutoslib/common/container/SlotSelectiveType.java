package com.hutoslib.common.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlotSelectiveType extends Slot {
	int limit;
	Class<? extends Item> itemType;

	public SlotSelectiveType(Container iItemHandlerModifiable, Class<? extends Item> itemType, int index, int limit,
			int xPosition, int yPosition) {
		super(iItemHandlerModifiable, index, xPosition, yPosition);
		this.itemType = itemType;
		this.limit = limit;
	}

	@Override
	public int getMaxStackSize() {
		return limit;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return itemType.isInstance(stack.getItem());
	}

}
