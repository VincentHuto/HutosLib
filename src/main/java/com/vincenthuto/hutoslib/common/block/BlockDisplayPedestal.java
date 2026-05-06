package com.vincenthuto.hutoslib.common.block;

import com.mojang.serialization.MapCodec;
import com.vincenthuto.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.vincenthuto.hutoslib.common.block.entity.HLBlockEntityInit;
import com.vincenthuto.hutoslib.common.container.HLInvHelper;
import com.vincenthuto.hutoslib.common.network.VanillaPacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class BlockDisplayPedestal extends BaseEntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final MapCodec<BlockDisplayPedestal> CODEC = simpleCodec(BlockDisplayPedestal::new);
	private static final VoxelShape SHAPE_N = Stream
			.of(Block.box(3, 0, 3, 13, 4, 13), Block.box(4, 4, 4, 12, 11, 12), Block.box(3, 11, 3, 13, 15, 13))
			.reduce((v1, v2) -> {
				return Shapes.join(v1, v2, BooleanOp.OR);
			}).get();

	public BlockDisplayPedestal(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));

	}

	@Override
	public MapCodec<BlockDisplayPedestal> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_,
			CollisionContext p_60558_) {
		return SHAPE_N;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState p_153183_,
			BlockEntityType<T> p_153184_) {
		return level.isClientSide
				? createTickerHelper(p_153184_, HLBlockEntityInit.display_pedestal.get(),
						DisplayPedestalBlockEntity::animTick)
				: null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DisplayPedestalBlockEntity(pos, state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos worldPosition, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(worldPosition) instanceof DisplayPedestalBlockEntity te && !te.inventory.isEmpty()) {
				double d0 = Mth.randomBetween(level.random, -0.2F, 0.2F);
				double d1 = Mth.randomBetween(level.random, -0.2F, 0.2F);
				double d2 = Mth.randomBetween(level.random, -0.2F, 0.2F);
				level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1,
						worldPosition.getZ(), te.inventory.get(0), d0, d1, d2));
			}
		}
		super.onRemove(state, level, worldPosition, newState, movedByPiston);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player,
			BlockHitResult result) {
		if (player.isShiftKeyDown()) {
			DisplayPedestalBlockEntity te = (DisplayPedestalBlockEntity) world.getBlockEntity(pos);
			if (te != null) {
				HLInvHelper.withdrawFromInventory(te, player);
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult result) {
		DisplayPedestalBlockEntity te = (DisplayPedestalBlockEntity) world.getBlockEntity(pos);
		if (te == null) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (player.isShiftKeyDown()) {
			HLInvHelper.withdrawFromInventory(te, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return ItemInteractionResult.SUCCESS;
		} else {
			boolean hit = te.addItem(player, stack, hand);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return hit ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
	}

}
