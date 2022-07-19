package com.vincenthuto.hutoslib.common.item;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.client.render.item.RenderItemArmBanner;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.Item.Properties;

public class ItemArmBanner extends Item implements IBannerSlotItem {
	public static final Capability<IBannerSlotItem> BANNER_SLOT_ITEM = CapabilityManager.get(new CapabilityToken<>() {
	});

	public ArmorMaterial material;
	ResourceLocation modellocation;

	public ItemArmBanner(Properties prop, ArmorMaterial materialIn, ResourceLocation modellocation) {
		super(prop.stacksTo(1));
		this.material = materialIn;
		this.modellocation = modellocation;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return stack.getTagElement("BlockEntityTag") != null ? this.getDescriptionId() + '.' + getColor(stack).getName()
				: super.getDescriptionId(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
	}

	@Override

	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		playerIn.startUsingItem(handIn);
		return InteractionResultHolder.consume(itemstack);
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {

		return ForgeRegistries.ITEMS.tags().getTag(ItemTags.PLANKS).contains(repair.getItem())
				|| super.isValidRepairItem(toRepair, repair);
	}

	public static DyeColor getColor(ItemStack stack) {
		return DyeColor.byId(stack.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(RenderPropArmBanner.INSTANCE);

	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundTag nbt) {
		return new ICapabilityProvider() {
			final LazyOptional<IBannerSlotItem> extensionSlotInstance = LazyOptional.of(() -> ItemArmBanner.this);

			@Override
			@Nonnull
			public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {
				if (cap == BANNER_SLOT_ITEM)
					return extensionSlotInstance.cast();
				return LazyOptional.empty();
			}
		};
	}

	public ResourceLocation getTexture() {

		return modellocation;
	}
}

class RenderPropArmBanner implements IClientItemExtensions {

	public static RenderPropArmBanner INSTANCE = new RenderPropArmBanner();

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return new RenderItemArmBanner(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
				Minecraft.getInstance().getEntityModels());
	}

}
