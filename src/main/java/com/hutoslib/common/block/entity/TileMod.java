package com.hutoslib.common.block.entity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileMod extends BlockEntity {

	public TileMod(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	@Override
	public CompoundTag save(CompoundTag p_58888_) {
		CompoundTag ret = super.save(p_58888_);
		writePacketNBT(ret);
		return super.save(p_58888_);
	}

	private boolean isVanilla = getClass().getName().startsWith("net.minecraft.");

	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return isVanilla ? (oldState.getBlock() != newSate.getBlock()) : oldState != newSate;
	}

	@Override
	public void load(CompoundTag p_155245_) {
		super.load(p_155245_);
		readPacketNBT(p_155245_);
	}

	@Nonnull
	@Override
	public final CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	public void writePacketNBT(CompoundTag cmp) {
	}

	public void readPacketNBT(CompoundTag cmp) {
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		writePacketNBT(tag);
		return new ClientboundBlockEntityDataPacket(worldPosition, -999, tag);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		readPacketNBT(pkt.getTag());

	}

}
