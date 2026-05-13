package com.vincenthuto.hutoslib.common.util;

public interface INBTSerializable<T> {
	T serializeNBT(net.minecraft.core.HolderLookup.Provider provider);

	void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, T nbt);
}
