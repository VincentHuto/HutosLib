package com.hutoslib.common.item;

import com.hutoslib.common.block.HutosLibBlockMaterials;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.Tag;
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

public class ItemKnapper extends DiggerItem {
	private static final Tag<Block> EFFECTIVE_ON = HutosLibBlockMaterials.knapper;
	private float speed;

	public ItemKnapper(float speedIn, float attackDamageIn, float attackSpeedIn, Tier tier, Properties builderIn) {
		super(attackDamageIn, -2.8f, tier, EFFECTIVE_ON, builderIn);
		this.speed = speedIn;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level lvl, Player p_41433_, InteractionHand p_41434_) {
		Minecraft mc = Minecraft.getInstance();
		if (lvl.isClientSide) {
//			mc.setScreen(new GuiGuidePage(0, EnumTomeCatagories.MAIN, "Title", "Subtitle", new ItemStack(Items.STONE),
//					"2fffffffffffffffffffffffffffffffffff222"));
		}
		return super.use(lvl, p_41433_, p_41434_);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return EFFECTIVE_ON.contains(state.getBlock()) ? speed : 0.5f;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
		return HutosLibBlockMaterials.DEFAULT_KNAPPER_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (EFFECTIVE_ON.contains(state.getBlock())) {
			ItemEntity ent = new ItemEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					new ItemStack(HutosLibItemInit.obsidian_flakes.get(), worldIn.random.nextInt(3)));
			worldIn.addFreshEntity(ent);
		}
		return super.mineBlock(stack, worldIn, state, pos, entityLiving);
	}
}
