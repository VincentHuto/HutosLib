package com.vincenthuto.hutoslib.common.container;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.vincenthuto.hutoslib.common.banner.BannerFinder;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketContainerSlot;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class BannerSlotContainer extends RecipeBookMenu<CraftingContainer> {

	private static class Bridge extends CraftingMenu {
		public static void slotChangedCraftingGridAccessor(AbstractContainerMenu container, Level level, Player player,
				CraftingContainer craftingInventory, ResultContainer craftResultInventory) {
			CraftingMenu.slotChangedCraftingGrid(container, level, player, craftingInventory, craftResultInventory);
		}

		private Bridge(int p_39353_, Inventory p_39354_) {
			super(p_39353_, p_39354_);
			throw new IllegalStateException("Not instantiable.");
		}
	}

	@SuppressWarnings("unused")
	private interface SlotFactory<T extends Slot> {
		T create(IBannerSlot slot, int x, int y);
	}

	private final IBannerSlot extensionSlot;
	private final CraftingContainer craftingInventory = new CraftingContainer(this, 2, 2);
	private final ResultContainer craftResultInventory = new ResultContainer();
	private final Player player;

	public BannerSlotContainer(int id, Inventory playerInventory) {
		super(HlContainerInit.banner_slot_container.get(), id);
		this.player = playerInventory.player;
		this.addSlot(
				new ResultSlot(playerInventory.player, this.craftingInventory, this.craftResultInventory, 0, 154, 28));

		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.addSlot(new Slot(this.craftingInventory, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int k = 0; k < 4; ++k) {
			final EquipmentSlot equipmentslot = InventoryMenu.SLOT_IDS[k];
			this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
					return Pair.of(InventoryMenu.BLOCK_ATLAS,
							InventoryMenu.TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
				}

				/**
				 * Return whether this slot's stack can be taken from this slot.
				 */
				@Override
				public boolean mayPickup(Player p_39744_) {
					ItemStack itemstack = this.getItem();
					return !itemstack.isEmpty() && !p_39744_.isCreative()
							&& EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(p_39744_);
				}

				/**
				 * Check if the stack is allowed to be placed in this slot, used for armor slots
				 * as well as furnace fuel.
				 */
				@Override
				public boolean mayPlace(ItemStack p_39746_) {
					return p_39746_.canEquip(equipmentslot, player);
				}

				@Override
				public void set(ItemStack p_219985_) {
					ItemStack itemstack = this.getItem();
					super.set(p_219985_);
					player.onEquipItem(equipmentslot, itemstack, p_219985_);
				}
			});
		}

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
		}

		this.addSlot(new Slot(playerInventory, 40, 77, 62) {
			{
				setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			}
		});

		BannerExtensionSlot container = playerInventory.player.getCapability(BannerExtensionSlot.CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Item handler not present."));

		extensionSlot = container.getBanner();

		this.addSlot(new BannerSlot(BannerSlotContainer.this.extensionSlot, 77, 44));

		if (playerInventory.player.level.isClientSide) {
			HLPacketHandler.MAINCHANNEL.sendToServer(new PacketContainerSlot());
		}
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container != this.craftResultInventory && super.canTakeItemForPickAll(stack, slotIn);
	}

	@Override
	public void clearCraftingContent() {
		this.craftResultInventory.clearContent();
		this.craftingInventory.clearContent();
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents itemHelperIn) {
		this.craftingInventory.fillStackedContents(itemHelperIn);
	}

	@Override
	public int getGridHeight() {
		return this.craftingInventory.getHeight();
	}

	@Override
	public int getGridWidth() {
		return this.craftingInventory.getWidth();
	}

	@Override
	public List<RecipeBookCategories> getRecipeBookCategories() {
		return Lists.newArrayList(RecipeBookCategories.CRAFTING_SEARCH, RecipeBookCategories.CRAFTING_EQUIPMENT,
				RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC,
				RecipeBookCategories.CRAFTING_REDSTONE);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	public int getResultSlotIndex() {
		return 0;
	}

	@Override
	public int getSize() {
		return 5;
	}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(pIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			EquipmentSlot equipmentslot = LivingEntity.getEquipmentSlotForItem(itemstack);
			if (pIndex == 0) {
				if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (pIndex >= 1 && pIndex < 5) {
				if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (pIndex >= 5 && pIndex < 9) {
				if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR
					&& !this.slots.get(8 - equipmentslot.getIndex()).hasItem()) {
				int i = 8 - equipmentslot.getIndex();
				if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentslot == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
				if (!this.moveItemStackTo(itemstack1, 45, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (pIndex >= 9 && pIndex < 36) {
				if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (pIndex >= 36 && pIndex < 45) {
				if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(pPlayer, itemstack1);
			if (pIndex == 0) {
				pPlayer.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean recipeMatches(Recipe<? super CraftingContainer> recipeIn) {
		return recipeIn.matches(this.craftingInventory, this.player.level);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);

		this.craftResultInventory.clearContent();

		if (!playerIn.level.isClientSide) {
			this.clearContainer(playerIn, this.craftingInventory);
			BannerFinder.sendSync(playerIn);
		}
	}

	@Override
	public boolean shouldMoveToInventory(int slot) {
		return slot != this.getResultSlotIndex();
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		Bridge.slotChangedCraftingGridAccessor(this, this.player.level, this.player, this.craftingInventory,
				this.craftResultInventory);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
}