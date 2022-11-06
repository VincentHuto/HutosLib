package com.vincenthuto.hutoslib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class DimensionalPosition implements INBTSerializable<CompoundTag> {

	public static DimensionalPosition fromNBT(CompoundTag nbt) {
		DimensionalPosition dp = new DimensionalPosition();
		dp.deserializeNBT(nbt);
		return dp;
	}
	private ResourceLocation dimension;

	private BlockPos position;
	public DimensionalPosition() {
	}

	/*
	 * to get RL Player().level.dimension().location();
	 */
	public DimensionalPosition(ResourceLocation dim, BlockPos pos) {
		this.dimension = dim;
		this.position = pos;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("dim")) {
			ResourceLocation dim = new ResourceLocation(nbt.getString("dim"));
			this.dimension = dim;
		}

		if (nbt.contains("pos")) {
			CompoundTag bPosNbt = nbt.getCompound("pos");
			BlockPos bPos = NbtUtils.readBlockPos(bPosNbt);
			this.position = bPos;
		}
	}

	public ResourceLocation getDimension() {
		return this.dimension;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("dim", dimension.toString());
		CompoundTag posNbt = NbtUtils.writeBlockPos(position);
		nbt.put("pos", posNbt);
		return nbt;
	}
}