package com.vincenthuto.hutoslib.common.banner;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.vincenthuto.hutoslib.common.container.BannerExtensionSlot;
import com.vincenthuto.hutoslib.common.container.BannerSlotItemHandler;
import com.vincenthuto.hutoslib.common.item.ItemArmBanner;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketBannerChange;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.network.PacketDistributor;

public class BannerFinderBannerSlot extends BannerFinder {

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
		public boolean isHidden() {
			return false;
		}

		@Override
		public void setBanner(ItemStack stack) {
			slot.setContents(stack);
		}

		@Override
		public void syncToClients() {
			LivingEntity thePlayer = slot.getContainer().getOwner();
			if (thePlayer.level.isClientSide)
				return;
			PacketBannerChange message = new PacketBannerChange(thePlayer, "banner_slot", new JsonPrimitive(0),
					slot.getContents());
			HLPacketHandler.MAINCHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> thePlayer),
					message);
		}
	}

	public static final Capability<BannerExtensionSlot> BANNER_SLOT_ITEM =CapabilityManager.get(new CapabilityToken<>(){});

	public static void initFinder() {
		BannerFinder.addFinder(new BannerFinderBannerSlot());
	}

	@Override
	public Optional<? extends BannerGetter> findStack(LivingEntity player, boolean allowCosmetic) {
		return BannerExtensionSlot.get(player).resolve()
				.flatMap(ext -> ext.getSlots().stream()
						.filter(slot -> slot.getContents().getItem() instanceof ItemArmBanner)
						.map(slot -> new ExtensionSlotBannerGetter(player, slot)).findFirst());
	}

	@Override
	public String getName() {
		return "banner_slot";
	}

	@Override
	protected Optional<BannerGetter> getSlotFromId(Player player, JsonElement packetData) {
		return BannerExtensionSlot.get(player).resolve().map(BannerExtensionSlot::getSlots)
				.map(slots -> slots.get(packetData.getAsInt()))
				.map(slot -> new ExtensionSlotBannerGetter(player, slot));
	}
}
