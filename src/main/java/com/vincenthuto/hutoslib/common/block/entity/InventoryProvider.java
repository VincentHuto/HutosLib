package com.vincenthuto.hutoslib.common.block.entity;

import net.minecraftforge.items.IItemHandlerModifiable;

public interface InventoryProvider
{

    IItemHandlerModifiable getInventory();

    default boolean isDummy() { return false; }
}