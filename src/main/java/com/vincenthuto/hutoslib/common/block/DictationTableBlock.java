package com.vincenthuto.hutoslib.common.block;

import net.minecraft.world.level.material.Fluids;
import com.vincenthuto.hutoslib.common.book.FieldNotes;
import com.vincenthuto.hutoslib.common.item.ItemGuideBook;

import com.vincenthuto.hutoslib.common.block.entity.DictationTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DictationTableBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DictationTableBlock() {
        this(BlockBehaviour.Properties.of().strength(2.0F, 6.0F).sound(SoundType.WOOD).noOcclusion());
    }
    public DictationTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(WATERLOGGED, false));
    }
    public boolean supportsDictation() { return true; }
    public int pendingGlowColor() { return 0; }
    public double bookHeight() { return 1.25; }
    public float bookRotationOffset() { return 90; }

    private static void removeStoredLiber(Level level, BlockPos pos, Player player, DictationTableBlockEntity table) {
        ItemStack liber = table.removeBook();
        if (!liber.isEmpty()) {
            if (!player.getInventory().add(liber)) {
                Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), liber);
            }
            player.displayClientMessage(Component.translatable("message.hutoslib.book.removed"), true);
        }
    }

    private void useStoredBook(Level level, BlockPos pos, Player player, DictationTableBlockEntity table) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) removeStoredLiber(level, pos, player, table);
        } else if (supportsDictation() && FieldNotes.hasPending(player, table.getBook(), this)) {
            if (player instanceof ServerPlayer serverPlayer) {
                FieldNotes.dictate(serverPlayer, table.getBook(), this);
                table.setChanged();
                level.sendBlockUpdated(pos, table.getBlockState(), table.getBlockState(), 3);
            }
        } else if (level.isClientSide) {
            com.vincenthuto.hutoslib.client.screen.guide.BookReaderScreen.openPlaced(pos, table.getBook());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DictationTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof DictationTableBlockEntity table && !table.getBook().isEmpty()) {
            useStoredBook(level, pos, player, table);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof DictationTableBlockEntity table) {
            ItemStack storedLiber = table.getBook();
            if (stack.getItem() instanceof ItemGuideBook && storedLiber.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack placed = stack.copyWithCount(1);
                    table.setBook(placed);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.displayClientMessage(Component.translatable("message.hutoslib.book.placed"), true);
                }
                return ItemInteractionResult.SUCCESS;
            }

            if (!storedLiber.isEmpty()) {
                useStoredBook(level, pos, player, table);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof DictationTableBlockEntity table) {
            ItemStack liber = table.removeBook();
            if (!liber.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), liber);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
			BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

}
