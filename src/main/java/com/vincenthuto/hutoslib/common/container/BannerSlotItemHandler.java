package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.vincenthuto.hutoslib.common.banner.BannerSlotCapability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class BannerSlotItemHandler implements IBannerSlot {
	protected final IBannerContainer owner;
	protected final ResourceLocation slotType;
	protected final int slot;
	protected final IItemHandlerModifiable inventory;

	public BannerSlotItemHandler(IBannerContainer owner, ResourceLocation slotType, IItemHandlerModifiable inventory,
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
	public ResourceLocation getType() {
		return slotType;
	}

	private void notifyEquip(ItemStack stack) {
		stack.getCapability(BannerSlotCapability.INSTANCE, null).ifPresent((extItem) -> {
			extItem.onEquipped(stack, this);
		});
	}

	private void notifyUnequip(ItemStack stack) {
		stack.getCapability(BannerSlotCapability.INSTANCE, null).ifPresent((extItem) -> {
			extItem.onUnequipped(stack, this);
		});
	}

	@Override
	public void onContentsChanged() {
		owner.onContentsChanged(this);
	}

	public void onWornTick() {
		ItemStack stack = getContents();
		if (stack.isEmpty())
			return;
		stack.getCapability(BannerSlotCapability.INSTANCE, null).ifPresent((extItem) -> {
			extItem.onWornTick(stack, this);
		});
	}

	@Override
	public void setContents(@Nonnull ItemStack stack) {
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
