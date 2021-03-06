package com.vincenthuto.hutoslib.common.item;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.Item.Properties;

public class ItemKnapper extends DiggerItem {
	public static final ToolAction KNAPPER_DIG = ToolAction.get("knapper_dig");
	public static final Set<ToolAction> DEFAULT_KNAPPER_ACTIONS = Stream.of(KNAPPER_DIG)
			.collect(Collectors.toCollection(Sets::newIdentityHashSet));
	public static final Material OBSIDIAN = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
	public static TagKey<Block> EFFECTIVE_ON = TagKey.create(Registry.BLOCK_REGISTRY,
			new ResourceLocation("mineable/knapper"));

	private float speed;

	public ItemKnapper(float speedIn, float attackDamageIn, float attackSpeedIn, Tier tier, Properties builderIn) {
		super(attackDamageIn, -2.8f, tier, EFFECTIVE_ON, builderIn);
		this.speed = speedIn;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player p_41433_, InteractionHand p_41434_) {
		return super.use(lvl, p_41433_, p_41434_);

	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return ForgeRegistries.BLOCKS.tags().getTag(EFFECTIVE_ON).contains(state.getBlock()) ? speed : 0.5f;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
		return DEFAULT_KNAPPER_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (ForgeRegistries.BLOCKS.tags().getTag(EFFECTIVE_ON).contains(state.getBlock())) {
			ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					new ItemStack(HLItemInit.obsidian_flakes.get(), worldIn.random.nextInt(3)));
			worldIn.addFreshEntity(ent);
		}
		return super.mineBlock(stack, worldIn, state, pos, entityLiving);
	}
}
