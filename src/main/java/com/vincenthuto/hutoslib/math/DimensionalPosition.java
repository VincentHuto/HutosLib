package com.vincenthuto.hutoslib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import com.vincenthuto.hutoslib.common.util.INBTSerializable;

import java.util.stream.Stream;

public class DimensionalPosition implements INBTSerializable<CompoundTag> {

	public static DimensionalPosition fromNBT(CompoundTag nbt) {
		return fromNBT(nbt, HolderLookup.Provider.create(Stream.empty()));
	}

	public static DimensionalPosition fromNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		DimensionalPosition dp = new DimensionalPosition();
		dp.deserializeNBT(provider, nbt);
		return dp;
	}
	private Identifier dimension;

	private BlockPos position;
	public DimensionalPosition() {
	}

	/*
	 * to get RL Player().level.dimension().identifier();
	 */
	public DimensionalPosition(Identifier dim, BlockPos pos) {
		this.dimension = dim;
		this.position = pos;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		if (nbt.contains("dim")) {
			Identifier parsed = nbt.getString("dim").map(Identifier::tryParse).orElse(null);
			if (parsed != null) {
				this.dimension = parsed;
			}
		}

		int x = nbt.getInt("x").orElse(0);
		int y = nbt.getInt("y").orElse(0);
		int z = nbt.getInt("z").orElse(0);
		this.position = new BlockPos(x, y, z);
	}

	public Identifier getDimension() {
		return this.dimension;
	}

	public BlockPos getPosition() {
		return this.position;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		if (dimension != null) {
			nbt.putString("dim", dimension.toString());
		}
		if (position != null) {
			nbt.putInt("x", position.getX());
			nbt.putInt("y", position.getY());
			nbt.putInt("z", position.getZ());
		}
		return nbt;
	}
}
