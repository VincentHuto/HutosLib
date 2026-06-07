package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.screen.lightning.LightningTesterItemScreen;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.lightning.LightningTesterSpawner;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemLightningTester extends Item {

	public ItemLightningTester(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
			InteractionHand usedHand) {
		if (player.isShiftKeyDown()) {
			if (player.level().isClientSide) {
				LightningTesterItemScreen.open(usedHand, LightningTestConfig.fromItem(stack));
			}
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		if (player.level() instanceof ServerLevel level) {
			LightningTesterSpawner.spawn(level, (ServerPlayer) player, player.getEyePosition(),
					target.position().add(0, target.getBbHeight() * 0.5, 0), LightningTestConfig.fromItem(stack));
		}
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		LightningTestConfig config = LightningTestConfig.fromItem(stack);
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) {
				LightningTesterItemScreen.open(usedHand, config);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		if (level instanceof ServerLevel serverLevel) {
			Vec3 start = player.getEyePosition();
			LightningTesterSpawner.spawn(serverLevel, (ServerPlayer) player, start,
					start.add(player.getLookAngle().scale(config.range())), config);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		ItemStack stack = context.getItemInHand();
		LightningTestConfig config = LightningTestConfig.fromItem(stack);
		if (player.isShiftKeyDown()) {
			if (context.getLevel().isClientSide) {
				LightningTesterItemScreen.open(context.getHand(), config);
			}
			return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
		}
		if (context.getLevel() instanceof ServerLevel level) {
			LightningTesterSpawner.spawn(level, (ServerPlayer) player, player.getEyePosition(),
					Vec3.atCenterOf(context.getClickedPos()), config);
		}
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
	}
}
