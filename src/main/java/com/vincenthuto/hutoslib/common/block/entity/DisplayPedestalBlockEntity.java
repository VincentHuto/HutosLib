package com.vincenthuto.hutoslib.common.block.entity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayPedestalBlockEntity extends TileSimpleInventory {

	public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
		super(HutosLibBlockEntityInit.display_pedestal.get(), pos, state);
	}

	public static void animTick(Level level, BlockPos pos, BlockState state, DisplayPedestalBlockEntity ent) {

	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		return super.save(compound);
	}



	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		super.getUpdatePacket();
		CompoundTag nbtTag = new CompoundTag();
		nbtTag.merge(itemHandler.serializeNBT());
		return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, nbtTag);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		super.onDataPacket(net, pkt);
		itemHandler = createItemHandler();
		itemHandler.deserializeNBT(tag);

	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	protected SimpleItemStackHandler createItemHandler() {
		return new SimpleItemStackHandler(this, false) {
			@Override
			protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
				return 1;
			}
		};
	}

	public boolean isEmpty() {
		for (int i = 0; i < getSizeInventory(); i++)
			if (!itemHandler.getStackInSlot(i).isEmpty())
				return false;

		return true;
	}

}
