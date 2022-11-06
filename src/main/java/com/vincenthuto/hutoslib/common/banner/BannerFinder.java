package com.vincenthuto.hutoslib.common.banner;

import java.util.Optional;
import java.util.function.IntFunction;

import com.google.gson.JsonElement;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class BannerFinder {
	public interface BannerGetter {
		ItemStack getBanner();

		default boolean isHidden() {
			return false;
		}

		default void setBanner(ItemStack stack) {
			// Defaults to "do nothing"
		}

		void syncToClients();
	}

	private static NonNullList<BannerFinder> instances = NonNullList.create();

	public static synchronized void addFinder(BannerFinder finder) {
		instances.add(0, finder);
	}

	public static Optional<? extends BannerGetter> findBanner(LivingEntity player) {
		return findBanner(player, false);
	}

	public static Optional<? extends BannerGetter> findBanner(LivingEntity player, boolean allowCosmetic) {
		return instances.stream().map(f -> f.findStack(player, allowCosmetic)).filter(Optional::isPresent).findFirst()
				.orElseGet(Optional::empty);
	}

	public static void sendSync(Player player) {
		findBanner(player).ifPresent(BannerGetter::syncToClients);
	}

	public static void setBannerFromPacket(Player player, String where, JsonElement slot, ItemStack stack) {
		for (BannerFinder finder : instances) {
			if (finder.getName().equals(where)) {
				finder.getSlotFromId(player, slot).ifPresent(getter -> getter.setBanner(stack));
			}
		}
	}

	protected final Optional<? extends BannerGetter> findBannerInInventory(IItemHandler inventory,
			IntFunction<? extends BannerGetter> getterFactory) {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack inSlot = inventory.getStackInSlot(i);
			if (inSlot.getCount() > 0) {
				if (inSlot.getItem() instanceof ItemArmBanner) {
					return Optional.of(getterFactory.apply(i));
				}
			}
		}
		return Optional.empty();
	}

	public abstract Optional<? extends BannerGetter> findStack(LivingEntity player, boolean allowCosmetic);

	public abstract String getName();

	protected abstract Optional<BannerGetter> getSlotFromId(Player player, JsonElement slot);
}
