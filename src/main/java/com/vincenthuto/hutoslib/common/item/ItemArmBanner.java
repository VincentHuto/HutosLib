package com.vincenthuto.hutoslib.common.item;

import java.util.List;
import java.util.function.Consumer;

import com.vincenthuto.hutoslib.client.render.item.RenderItemArmBanner;
import com.vincenthuto.hutoslib.common.container.IBannerSlotItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
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
DyeColor color = stack.get(DataComponents.BASE_COLOR);
return color != null ? color : DyeColor.WHITE;
}

public Holder<ArmorMaterial> material;
ResourceLocation modellocation;

public ItemArmBanner(Properties prop, Holder<ArmorMaterial> materialIn, ResourceLocation modellocation) {
super(prop.stacksTo(1));
this.material = materialIn;
this.modellocation = modellocation;
}

@Override
public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
}

@Override
public String getDescriptionId(ItemStack stack) {
DyeColor color = stack.get(DataComponents.BASE_COLOR);
return color != null ? this.getDescriptionId() + '.' + color.getName() : super.getDescriptionId(stack);
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
