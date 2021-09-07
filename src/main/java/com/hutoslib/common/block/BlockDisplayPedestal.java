package com.hutoslib.common.block;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.hutoslib.common.block.entity.DisplayPedestalBlockEntity;
import com.hutoslib.common.block.entity.HutosLibBlockEntityInit;
import com.hutoslib.common.network.VanillaPacketDispatcher;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
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

public class BlockDisplayPedestal extends BaseEntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
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
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_,
			CollisionContext p_60558_) {
		return SHAPE_N;
	}

	@Override
	public RenderShape getRenderShape(BlockState p_49232_) {
		return RenderShape.MODEL;
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState p_153183_,
			BlockEntityType<T> p_153184_) {
		return level.isClientSide
				? createTickerHelper(p_153184_, HutosLibBlockEntityInit.display_pedestal.get(),
						DisplayPedestalBlockEntity::animTick)
				: null;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult result) {

		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		DisplayPedestalBlockEntity te = (DisplayPedestalBlockEntity) level.getBlockEntity(pos);
		ItemStack stack = player.getMainHandItem();

		if (stack.isEmpty()) {
			InventoryHelper.withdrawFromInventory(te, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return InteractionResult.SUCCESS;
		}
		if (!stack.isEmpty() && !player.isCrouching()) {
			te.addItem(player, stack, hand);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return InteractionResult.SUCCESS;
		}

		return super.use(state, level, pos, player, hand, result);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DisplayPedestalBlockEntity(pos, state);
	}

}
