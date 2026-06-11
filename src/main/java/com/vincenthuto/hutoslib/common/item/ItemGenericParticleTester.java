package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.screen.particle.GenericParticleTesterItemScreen;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTesterSpawner;

import net.minecraft.server.level.ServerLevel;
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

public class ItemGenericParticleTester extends Item {
	public ItemGenericParticleTester(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
			InteractionHand usedHand) {
		GenericParticleTestConfig config = GenericParticleTestConfig.fromItem(stack);
		if (player.isShiftKeyDown()) {
			if (player.level().isClientSide) {
				GenericParticleTesterItemScreen.open(usedHand, config);
			}
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		if (player.level() instanceof ServerLevel level) {
			GenericParticleTesterSpawner.spawn(level, target.position().add(0, target.getBbHeight() * 0.5, 0),
					config);
		}
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		GenericParticleTestConfig config = GenericParticleTestConfig.fromItem(stack);
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) {
				GenericParticleTesterItemScreen.open(usedHand, config);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		if (level instanceof ServerLevel serverLevel) {
			Vec3 start = player.getEyePosition().add(player.getLookAngle().scale(config.range()));
			GenericParticleTesterSpawner.spawn(serverLevel, start, config);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		GenericParticleTestConfig config = GenericParticleTestConfig.fromItem(context.getItemInHand());
		if (player.isShiftKeyDown()) {
			if (context.getLevel().isClientSide) {
				GenericParticleTesterItemScreen.open(context.getHand(), config);
			}
			return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
		}
		if (context.getLevel() instanceof ServerLevel level) {
			Vec3 pos = Vec3.atCenterOf(context.getClickedPos())
					.add(Vec3.atLowerCornerOf(context.getClickedFace().getNormal()).scale(0.6D));
			GenericParticleTesterSpawner.spawn(level, pos, config);
		}
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
	}
}
