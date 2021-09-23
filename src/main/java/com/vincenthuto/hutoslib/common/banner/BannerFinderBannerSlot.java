package com.vincenthuto.hutoslib.common.banner;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.network.HutosLibPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketBannerChange;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class BannerFinderBannerSlot extends BannerFinder {

	@CapabilityInject(BannerExtensionSlot.class)
	public static void initFinder(Capability<?> cap) {
		BannerFinder.addFinder(new BannerFinderBannerSlot());
	}

	@Override
	protected Optional<BannerGetter> getSlotFromId(Player player, JsonElement packetData) {
		return BannerExtensionSlot.get(player).resolve().map(BannerExtensionSlot::getSlots)
				.map(slots -> slots.get(packetData.getAsInt()))
				.map(slot -> new ExtensionSlotBannerGetter(player, slot));
	}

	@Override
	public String getName() {
		return "banner_slot";
	}

	@Override
	public Optional<? extends BannerGetter> findStack(LivingEntity player, boolean allowCosmetic) {
		return BannerExtensionSlot.get(player).resolve()
				.flatMap(ext -> ext.getSlots().stream()
						.filter(slot -> slot.getContents().getItem() instanceof ItemArmBanner)
						.map(slot -> new ExtensionSlotBannerGetter(player, slot)).findFirst());
	}

	private static class ExtensionSlotBannerGetter implements BannerGetter {
		@SuppressWarnings("unused")
		private final LivingEntity player;
		private final BannerSlotItemHandler slot;

		private ExtensionSlotBannerGetter(LivingEntity player, BannerSlotItemHandler slot) {
			this.player = player;
			this.slot = slot;
		}

		@Override
		public ItemStack getBanner() {
			return slot.getContents();
		}

		@Override
		public void setBanner(ItemStack stack) {
			slot.setContents(stack);
		}

		@Override
		public boolean isHidden() {
			return false;
		}

		@Override
		public void syncToClients() {
			LivingEntity thePlayer = slot.getContainer().getOwner();
			if (thePlayer.level.isClientSide)
				return;
			PacketBannerChange message = new PacketBannerChange(thePlayer, "banner_slot", new JsonPrimitive(0),
					slot.getContents());
			HutosLibPacketHandler.MAINCHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> thePlayer),
					message);
		}
	}
}
