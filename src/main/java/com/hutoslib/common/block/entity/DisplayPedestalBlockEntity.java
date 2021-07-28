package com.hutoslib.common.block.entity;

import java.util.Random;

import javax.annotation.Nonnull;

import com.hutoslib.client.particle.util.ParticleColor;
import com.hutoslib.common.network.HutosLibPacketHandler;
import com.hutoslib.math.Vector3;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayPedestalBlockEntity extends TileSimpleInventory {

	public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
		super(HutosLibBlockEntityInit.display_pedestal.get(), pos, state);
	}

	public static void animTick(Level level, BlockPos pos, BlockState state, DisplayPedestalBlockEntity ent) {
		Random rand = level.random;
		Vector3 posVec = Vector3.fromTileEntityCenter(ent).add(0, 0.5, 0);
		for (int i = 0; i < 10; i++) {
			Vector3 entVec = posVec;
			Vector3 endVec = posVec.add(rand.nextDouble() - rand.nextDouble(), 1,
					rand.nextDouble() - rand.nextDouble());

			HutosLibPacketHandler.sendLightningSpawn(entVec, endVec, 64.0f, (ResourceKey<Level>) level.dimension(),
					ParticleColor.RED, 3, 10, 9, 1.2f);
		}

	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		return super.save(compound);
	}

	@Override
	public void load(CompoundTag p_155245_) {
		super.load(p_155245_);
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
