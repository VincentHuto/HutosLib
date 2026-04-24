package com.vincenthuto.hutoslib.common.container;

import org.jspecify.annotations.NonNull;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@SuppressWarnings("deprecation")
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

	@NonNull
	@Override
	public IBannerContainer getContainer() {
		return owner;
	}

	@NonNull
	@Override
	public ItemStack getContents() {
		return inventory.getStackInSlot(slot);
	}

	@NonNull
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
	public void setContents(@NonNull ItemStack stack) {
		ItemStack oldStack = getContents();
		if (oldStack == stack)
			return;
		if (!oldStack.isEmpty())
			notifyUnequip(oldStack);
		inventory.setStackInSlot(slot, stack);
		if (!stack.isEmpty())
			notifyEquip(stack);
	}

}
