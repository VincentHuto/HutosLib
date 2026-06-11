package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.client.screen.tendril.TendrilTesterItemScreen;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectSpawner;
import com.vincenthuto.hutoslib.common.tendril.TendrilTesterOrigin;
import com.vincenthuto.hutoslib.common.template.EffectTemplateType;

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

public class ItemTendrilTester extends Item {
	public ItemTendrilTester(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
			InteractionHand usedHand) {
		TendrilEffectConfig config = configForUse(stack, player);
		if (player.isShiftKeyDown()) {
			if (player.level().isClientSide) {
				TendrilTesterItemScreen.open(usedHand, config);
			}
			return InteractionResult.sidedSuccess(player.level().isClientSide);
		}
		if (player.level() instanceof ServerLevel level) {
			TendrilEffectSpawner.spawn(level, (ServerPlayer) player,
					TendrilTesterOrigin.playerHandPoint(player, usedHand),
					new TendrilAnchor.Entity(target.getId(), TendrilAnchor.AnchorPoint.CENTER, Vec3.ZERO), config);
		}
		return InteractionResult.sidedSuccess(player.level().isClientSide);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		TendrilEffectConfig config = configForUse(stack, player);
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) {
				TendrilTesterItemScreen.open(usedHand, config);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		if (level instanceof ServerLevel serverLevel) {
			Vec3 end = player.getEyePosition().add(player.getLookAngle().scale(config.range()));
			TendrilEffectSpawner.spawn(serverLevel, (ServerPlayer) player,
					TendrilTesterOrigin.playerHandPoint(player, usedHand),
					new TendrilAnchor.Point(end), config);
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
		TendrilEffectConfig config = configForUse(stack, player);
		if (player.isShiftKeyDown()) {
			if (context.getLevel().isClientSide) {
				TendrilTesterItemScreen.open(context.getHand(), config);
			}
			return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
		}
		if (context.getLevel() instanceof ServerLevel level) {
			TendrilEffectSpawner.spawn(level, (ServerPlayer) player,
					TendrilTesterOrigin.playerHandPoint(player, context.getHand()),
					new TendrilAnchor.Point(Vec3.atCenterOf(context.getClickedPos())), config);
		}
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
	}

	private static TendrilEffectConfig configForUse(ItemStack stack, Player player) {
		ItemStack offhand = player.getOffhandItem();
		if (EffectTemplateType.TENDRIL.matches(offhand)) {
			return TendrilEffectConfig.fromItem(offhand);
		}
		return TendrilEffectConfig.fromItem(stack);
	}
}
