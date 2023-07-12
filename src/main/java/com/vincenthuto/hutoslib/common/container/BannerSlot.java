package com.vincenthuto.hutoslib.common.container;

import javax.annotation.Nonnull;

import com.vincenthuto.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BannerSlot extends Slot {
	public static final ResourceLocation SLOT_BACKGROUND = HutosLib.rloc(
			"gui/empty_banner_slot_background");

	private static Container emptyInventory = new SimpleContainer(0);
	private final IBannerSlot slot;

	public BannerSlot(IBannerSlot slot, int x, int y) {
		super(emptyInventory, 0, x, y);
		this.slot = slot;
		setBackground(InventoryMenu.BLOCK_ATLAS, SLOT_BACKGROUND);
	}

	public IBannerSlot getExtensionSlot() {
		return slot;
	}

	@Override
	@Nonnull
	public ItemStack getItem() {
		return slot.getContents();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(@Nonnull ItemStack stack) {
		return 1;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof BannerSlot && ((BannerSlot) other).getExtensionSlot() == this.slot;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return slot.canUnequip(slot.getContents());
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return slot.canEquip(stack);
	}

	@Override
	public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {

	}

	@Override
	@Nonnull
	public ItemStack remove(int amount) {
		ItemStack itemstack = slot.getContents();

		int available = Math.min(itemstack.getCount(), amount);
		int remaining = itemstack.getCount() - available;

		ItemStack split = itemstack.copy();
		split.setCount(available);
		itemstack.setCount(remaining);

		if (remaining <= 0)
			slot.setContents(ItemStack.EMPTY);

		this.setChanged();

		return split;
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
		slot.setContents(stack);
		this.setChanged();
	}
}
