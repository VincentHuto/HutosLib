package com.vincenthuto.hutoslib.common.block;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.client.screen.particle.GenericParticleTesterBlockScreen;
import com.vincenthuto.hutoslib.common.block.entity.GenericParticleTesterBlockEntity;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTesterSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockGenericParticleTester extends BaseEntityBlock {
	private static final MapCodec<BlockGenericParticleTester> CODEC = simpleCodec(BlockGenericParticleTester::new);
	private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

	public BlockGenericParticleTester(Properties properties) {
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
		return new GenericParticleTesterBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null
				: createTickerHelper(blockEntityType, HLBlockEntityInit.generic_particle_tester.get(),
						GenericParticleTesterBlockEntity::serverTick);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		if (!(level.getBlockEntity(pos) instanceof GenericParticleTesterBlockEntity blockEntity)) {
			return InteractionResult.PASS;
		}
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) {
				GenericParticleTesterBlockScreen.open(pos, blockEntity.getConfig());
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		if (level instanceof ServerLevel serverLevel) {
			GenericParticleTesterSpawner.spawn(serverLevel, GenericParticleTesterBlockEntity.spawnPos(pos),
					blockEntity.getConfig());
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
