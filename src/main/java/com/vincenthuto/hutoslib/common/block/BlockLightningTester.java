package com.vincenthuto.hutoslib.common.block;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.screen.lightning.LightningTesterBlockScreen;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.block.entity.LightningTesterBlockEntity;
import com.vincenthuto.hutoslib.common.lightning.LightningTesterSpawner;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockLightningTester extends BaseEntityBlock {
	private static final MapCodec<BlockLightningTester> CODEC = simpleCodec(BlockLightningTester::new);
	private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

	public BlockLightningTester(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightningTesterBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null
				: createTickerHelper(blockEntityType, HLBlockEntityInit.lightning_tester.get(),
						LightningTesterBlockEntity::serverTick);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof LightningTesterBlockEntity blockEntity)) {
			return InteractionResult.PASS;
		}
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) {
				LightningTesterBlockScreen.open(pos, blockEntity.getConfig());
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		spawn(level, player, pos, blockEntity);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitResult) {
		InteractionResult result = useWithoutItem(state, level, pos, player, hitResult);
		return result.consumesAction() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	private static void spawn(Level level, Player player, BlockPos pos, LightningTesterBlockEntity blockEntity) {
		if (level instanceof ServerLevel serverLevel) {
			Vec3 start = Vec3.atCenterOf(pos);
			LightningTesterSpawner.spawn(serverLevel, (ServerPlayer) player, start, start.add(blockEntity.getConfig().targetOffset()),
					blockEntity.getConfig());
		}
	}
}
