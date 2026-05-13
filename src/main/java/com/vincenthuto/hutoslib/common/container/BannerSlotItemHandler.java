package com.vincenthuto.hutoslib.common.container;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class BannerSlotItemHandler implements IBannerSlot {
	protected final IBannerContainer owner;
	protected final Identifier slotType;
	protected final int slot;
	protected final IItemHandlerModifiable inventory;

	public BannerSlotItemHandler(IBannerContainer owner, Identifier slotType, IItemHandlerModifiable inventory,
			int slot) {
		this.owner = owner;
		this.slotType = slotType;
		this.slot = slot;
		this.inventory = inventory;
	}

	@Nonnull
	@Override
	public IBannerContainer getContainer() {
		return owner;
	}

	@Nonnull
	@Override
	public ItemStack getContents() {
		return inventory.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public Identifier getType() {
		return slotType;
	}

	private void notifyEquip(ItemStack stack) {
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			extItem.onEquipped(stack, this);
		}
	}

	private void notifyUnequip(ItemStack stack) {
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			extItem.onUnequipped(stack, this);
		}
	}

	@Override
	public void onContentsChanged() {
		owner.onContentsChanged(this);
	}

	public void onWornTick() {
		ItemStack stack = getContents();
		if (stack.isEmpty())
			return;
		if (stack.getItem() instanceof IBannerSlotItem extItem) {
			extItem.onWornTick(stack, this);
		}
	}

	@Override
	public void setContents(@Nonnull ItemStack stack) {
		ItemStack oldStack = getContents();
		ItemStack newStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		if (ItemStack.matches(oldStack, newStack))
			return;
		if (!oldStack.isEmpty())
			notifyUnequip(oldStack);
		inventory.setStackInSlot(slot, newStack);
		if (!newStack.isEmpty())
			notifyEquip(newStack);
	}

}
