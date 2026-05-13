package com.vincenthuto.hutoslib.common.container;

import com.vincenthuto.hutoslib.common.recipe.ArmBannerCraftRecipe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class BannerSlotContainer extends AbstractCraftingMenu {

	private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private static final Identifier[] TEXTURE_EMPTY_SLOTS = new Identifier[]{
		InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
		InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
		InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
		InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
	};

	private final IBannerSlot extensionSlot;
	private final Player player;

	@SuppressWarnings("unused")
	private interface SlotFactory<T extends Slot> {
		T create(IBannerSlot slot, int x, int y);
	}

	public BannerSlotContainer(int id, Inventory playerInventory) {
		super(HlContainerInit.banner_slot_container.get(), id, 2, 2);
		this.player = playerInventory.player;
		this.addSlot(
				new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));

		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.addSlot(new Slot(this.craftSlots, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int k = 0; k < 4; ++k) {
			final EquipmentSlot equipmentslot = SLOT_IDS[k];
			this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public Identifier getNoItemIcon() {
					return TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()];
				}

				@Override
				public boolean mayPickup(Player p_39744_) {
					ItemStack itemstack = this.getItem();
					if (!itemstack.isEmpty() && !p_39744_.isCreative()) {
						var enchantments = itemstack.get(DataComponents.ENCHANTMENTS);
						if (enchantments != null && enchantments.keySet().stream().anyMatch(h -> h.is(Enchantments.BINDING_CURSE))) {
							return false;
						}
					}
					return super.mayPickup(p_39744_);
				}

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
				setBackground(InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
			}
		});

		BannerExtensionSlot container = BannerExtensionSlot.get(playerInventory.player);
		extensionSlot = container.getBanner();

		this.addSlot(new BannerSlot(BannerSlotContainer.this.extensionSlot, 77, 44));

	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container != this.resultSlots && super.canTakeItemForPickAll(stack, slotIn);
	}

	public void clearCraftingContent() {
		this.resultSlots.clearContent();
		this.craftSlots.clearContent();
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedItemContents itemHelperIn) {
		this.craftSlots.fillStackedContents(itemHelperIn);
	}

	@Override
	public Slot getResultSlot() {
		return this.slots.get(0);
	}

	@Override
	public List<Slot> getInputGridSlots() {
		return this.slots.subList(1, 5);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

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
			EquipmentSlot equipmentslot = pPlayer.getEquipmentSlotForItem(itemstack);
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
			} else if (equipmentslot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR
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

	public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipeIn) {
		return recipeIn.value().matches(this.craftSlots.asCraftInput(), this.player.level());
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.resultSlots.clearContent();
		if (!playerIn.level().isClientSide()) {
			this.clearContainer(playerIn, this.craftSlots);
			BannerExtensionSlot.get(playerIn).syncToTracking();
		}
	}

	public boolean shouldMoveToInventory(int slot) {
		return slot != 0;
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		if (this.player.level() instanceof ServerLevel serverLevel) {
			ItemStack armBannerResult = ArmBannerCraftRecipe.assembleArmBanner(this.craftSlots.asCraftInput());
			if (!armBannerResult.isEmpty()) {
				this.resultSlots.setItem(0, armBannerResult);
				this.setRemoteSlot(0, armBannerResult);
				if (this.player instanceof ServerPlayer serverPlayer) {
					serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
							this.containerId, this.incrementStateId(), 0, armBannerResult));
				}
				return;
			}

			Bridge.slotChangedCraftingGridAccessor(this, serverLevel, this.player, this.craftSlots,
					this.resultSlots, null);
		}
	}

	private static class Bridge extends CraftingMenu {
		public static void slotChangedCraftingGridAccessor(AbstractContainerMenu container, ServerLevel level, Player player,
				CraftingContainer craftingInventory, ResultContainer craftResultInventory,
				RecipeHolder<CraftingRecipe> lastRecipe) {
			CraftingMenu.slotChangedCraftingGrid(container, level, player, craftingInventory, craftResultInventory, lastRecipe);
		}

		private Bridge(int p_39353_, Inventory p_39354_) {
			super(p_39353_, p_39354_);
			throw new IllegalStateException("Not instantiable.");
		}
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}

	@Override
	protected Player owner() {
		return this.player;
	}
}

