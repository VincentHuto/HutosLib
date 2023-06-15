package com.vincenthuto.hutoslib.common.container;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketSyncBannerSlotContents;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class BannerExtensionSlot implements IBannerContainer, INBTSerializable<CompoundTag> {

	public static class AttachHandlers {
		@SubscribeEvent
		public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			final Entity entity = event.getObject();
			if (entity instanceof Player || entity instanceof ArmorStand) {
				event.addCapability(CAPABILITY_ID, new ICapabilitySerializable<CompoundTag>() {
					final BannerExtensionSlot extensionContainer = new BannerExtensionSlot((LivingEntity) entity) {
						@Override
						public void onContentsChanged(BannerSlotItemHandler slot) {
							if (!getOwner().level().isClientSide)
								syncTo(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this::getOwner));
						}
					};

					final LazyOptional<BannerExtensionSlot> extensionContainerSupplier = LazyOptional
							.of(() -> extensionContainer);

					@Override
					public void deserializeNBT(CompoundTag nbt) {
						extensionContainer.deserializeNBT(nbt);
					}

					@Override
					public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability,
							@Nullable Direction facing) {
						if (CAPABILITY == capability)
							return extensionContainerSupplier.cast();

						return LazyOptional.empty();
					}

					@Override
					public CompoundTag serializeNBT() {
						return extensionContainer.serializeNBT();
					}
				});
			}
		}
	}

	public static class EventHandlers {
		@SubscribeEvent
		public void entityTick(TickEvent.PlayerTickEvent event) {
			if (event.phase != TickEvent.Phase.END)
				return;

			get(event.player).ifPresent(BannerExtensionSlot::tickAllSlots);
		}

		@SubscribeEvent
		public void joinWorld(PlayerEvent.PlayerChangedDimensionEvent event) {
			Player target = event.getEntity();
			if (target.level().isClientSide)
				return;
			get(target).ifPresent(BannerExtensionSlot::syncToSelf);
		}

		@SubscribeEvent
		public void joinWorld(PlayerEvent.PlayerLoggedInEvent event) {
			Player target = event.getEntity();
			if (target.level().isClientSide)
				return;
			get(target).ifPresent(BannerExtensionSlot::syncToSelf);
		}

		@SubscribeEvent
		public void playerClone(PlayerEvent.Clone event) {
			Player oldPlayer = event.getOriginal();
			oldPlayer.revive();
			Player newPlayer = event.getEntity();
			get(oldPlayer).ifPresent((oldBanner) -> {
				ItemStack stack = oldBanner.getBanner().getContents();
				get(newPlayer).map(newBanner -> {
					newBanner.getBanner().setContents(stack);
					return Unit.INSTANCE;
				}).orElseGet(() -> {
					if (stack.getCount() > 0) {
						oldPlayer.drop(stack, true, false);
					}
					return Unit.INSTANCE;
				});
			});
		}

		@SubscribeEvent
		public void playerDeath(LivingDropsEvent event) {
			LivingEntity entity = event.getEntity();

			get(entity).ifPresent((instance) -> {
				BannerSlotItemHandler banner = instance.getBanner();
				ItemStack stack = banner.getContents();
				if (EnchantmentHelper.hasVanishingCurse(stack)) {
					stack = ItemStack.EMPTY;
					banner.setContents(stack);
				}
				if (stack.getCount() > 0) {
					if (entity instanceof Player player) {
						if (!entity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
								&& !player.isSpectator()) {
							Collection<ItemEntity> old = entity.captureDrops(event.getDrops());
							player.drop(stack, true, false);
							entity.captureDrops(old);
							banner.setContents(ItemStack.EMPTY);
						}
					} else {
						entity.spawnAtLocation(stack);
						banner.setContents(ItemStack.EMPTY);
					}
				}
			});
		}

		@SubscribeEvent
		public void track(PlayerEvent.StartTracking event) {
			Entity target = event.getTarget();
			if (target.level().isClientSide)
				return;
			if (target instanceof Player) {
				get((LivingEntity) target).ifPresent(BannerExtensionSlot::syncToSelf);
			}
		}
	}

	private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(HutosLib.MOD_ID, "banner_slot");

	public static final Capability<BannerExtensionSlot> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static final ResourceLocation BANNER = new ResourceLocation("hutoslib", "banner");

	public static LazyOptional<BannerExtensionSlot> get(LivingEntity player) {
		return player.getCapability(CAPABILITY);
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AttachHandlers());
		MinecraftForge.EVENT_BUS.register(new EventHandlers());
	}

	private final LivingEntity owner;

	private final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			banner.onContentsChanged();
		}
	};

	private final BannerSlotItemHandler banner = new BannerSlotItemHandler(this, BANNER, inventory, 0);

	private final ImmutableList<BannerSlotItemHandler> slots = ImmutableList.of(banner);
	private BannerExtensionSlot(LivingEntity owner) {
		this.owner = owner;
	}
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		inventory.deserializeNBT(nbt);
	}
	@Nonnull
	public BannerSlotItemHandler getBanner() {
		return banner;
	}

	@Nonnull
	@Override
	public LivingEntity getOwner() {
		return owner;
	}

	@Nonnull
	@Override
	public ImmutableList<BannerSlotItemHandler> getSlots() {
		return slots;
	}

	@Override
	public void onContentsChanged(BannerSlotItemHandler slot) {

	}

	@Override
	public CompoundTag serializeNBT() {
		return inventory.serializeNBT();
	}

	public void setAll(NonNullList<ItemStack> stacks) {
		List<BannerSlotItemHandler> slots = getSlots();
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).setContents(stacks.get(i));
		}
	}

	protected void syncTo(PacketDistributor.PacketTarget target) {
		PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents((Player) owner, this);
		HLPacketHandler.MAINCHANNEL.send(target, message);
	}

	protected void syncTo(Player target) {
		PacketSyncBannerSlotContents message = new PacketSyncBannerSlotContents((Player) owner, this);
		HLPacketHandler.MAINCHANNEL.sendTo(message, ((ServerPlayer) target).connection.connection,
				NetworkDirection.PLAY_TO_CLIENT);
	}

	private void syncToSelf() {
		syncTo((Player) owner);
	}

	private void tickAllSlots() {
		for (BannerSlotItemHandler slot : slots) {
			slot.onWornTick();
		}
	}
}
