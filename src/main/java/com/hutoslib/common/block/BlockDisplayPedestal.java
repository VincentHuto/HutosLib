package com.hutoslib.common.block;

import java.util.stream.Stream;

import com.hutoslib.common.VanillaPacketDispatcher;
import com.hutoslib.common.container.InventoryHelper;
import com.hutoslib.common.tile.TileDisplayPedestal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDisplayPedestal extends Block {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE_N = Stream.of(Block.makeCuboidShape(3, 0, 3, 13, 4, 13),
			Block.makeCuboidShape(4, 4, 4, 12, 11, 12), Block.makeCuboidShape(3, 11, 3, 13, 15, 13))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public BlockDisplayPedestal(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));

	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE_N;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;
		TileDisplayPedestal te = (TileDisplayPedestal) worldIn.getTileEntity(pos);
		ItemStack stack = player.getHeldItemMainhand();

		if (stack.isEmpty()) {
			InventoryHelper.withdrawFromInventory(te, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return ActionResultType.SUCCESS;
		}
		// If there is something in your hand add it to the block if its not an //
		if (!stack.isEmpty() && !player.isCrouching()) {
			te.addItem(player, stack, handIn);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileDisplayPedestal();
	}

}
