package com.vincenthuto.hutoslib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;

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
	 * to get RL Player().level.dimension().location();
	 */
	public DimensionalPosition(Identifier dim, BlockPos pos) {
		this.dimension = dim;
		this.position = pos;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		if (nbt.contains("dim")) {
			Identifier parsed = Identifier.tryParse(nbt.getString("dim"));
			if (parsed != null) {
				this.dimension = parsed;
			}
		}

		NbtUtils.readBlockPos(nbt, "pos").ifPresent(pos -> this.position = pos);
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
			nbt.put("pos", NbtUtils.writeBlockPos(position));
		}
		return nbt;
	}
}