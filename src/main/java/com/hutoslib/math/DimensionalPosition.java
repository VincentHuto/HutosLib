package com.hutoslib.math;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class DimensionalPosition implements INBTSerializable<CompoundTag> {

	private ResourceLocation dimension;
	private BlockPos position;

	public DimensionalPosition() {
	}

	public DimensionalPosition(ResourceLocation dim, BlockPos pos) {
		this.dimension = dim;
		this.position = pos;
	}

	public static DimensionalPosition fromNBT(CompoundTag nbt) {
		DimensionalPosition dp = new DimensionalPosition();
		dp.deserializeNBT(nbt);

		return dp;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("dim", dimension.toString());
		CompoundTag posNbt = NbtUtils.writeBlockPos(position);
		nbt.put("pos", posNbt);
		return nbt;
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
}