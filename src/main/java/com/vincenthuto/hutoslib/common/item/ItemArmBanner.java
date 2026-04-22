package com.vincenthuto.hutoslib.common.item;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.client.render.item.RenderItemArmBanner;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ItemArmBanner extends Item implements IBannerSlotItem {

public static DyeColor getColor(ItemStack stack) {
return DyeColor.byId(stack.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
}

public ArmorMaterial material;
ResourceLocation modellocation;

public ItemArmBanner(Properties prop, ArmorMaterial materialIn, ResourceLocation modellocation) {
super(prop.stacksTo(1));
this.material = materialIn;
this.modellocation = modellocation;
}

@Override
public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
}

@Override
public String getDescriptionId(ItemStack stack) {
return stack.getTagElement("BlockEntityTag") != null ? this.getDescriptionId() + '.' + getColor(stack).getName()
: super.getDescriptionId(stack);
}

public ResourceLocation getTexture() {
return modellocation;
}

@Override
public void initializeClient(Consumer<IClientItemExtensions> consumer) {
super.initializeClient(consumer);
consumer.accept(RenderPropArmBanner.INSTANCE);
}

@Override
public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
return repair.is(ItemTags.PLANKS) || super.isValidRepairItem(toRepair, repair);
}

@Override
public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
ItemStack itemstack = playerIn.getItemInHand(handIn);
playerIn.startUsingItem(handIn);
return InteractionResultHolder.consume(itemstack);
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
