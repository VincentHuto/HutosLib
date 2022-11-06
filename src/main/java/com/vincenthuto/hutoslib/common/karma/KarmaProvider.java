package com.vincenthuto.hutoslib.common.karma;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class KarmaProvider implements ICapabilitySerializable<Tag> {
    public static final Capability<IKarma> KARMA_CAPA = CapabilityManager.get(new CapabilityToken<>(){});

	public static float getPlayerbloodAmount(Player player) {
		return player.getCapability(KARMA_CAPA).orElseThrow(IllegalStateException::new).getKarma();
	}
	Karma capability = new Karma();

	private LazyOptional<IKarma> instance = LazyOptional.of(() -> capability);

	@Override
	public void deserializeNBT(Tag nbt) {
		readNBT(KARMA_CAPA, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")),
				null, nbt);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == KARMA_CAPA ? instance.cast() : LazyOptional.empty();
	}

	public void readNBT(Capability<IKarma> capability, IKarma instance, Direction side, Tag nbt) {
		if (!(instance instanceof Karma))
			throw new IllegalArgumentException(
					"Can not deserialize to an instance that isn't the default implementation");
		if (nbt instanceof CompoundTag entry) {
			if (entry.contains("Active") && entry.contains("Amount")) {
				instance.setActive(entry.getBoolean("Active"));
				instance.setKarma(entry.getFloat("Amount"));
			}
		}

	}

	@Override
	public Tag serializeNBT() {
		return writeNBT(KARMA_CAPA,
				instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
	}

	public CompoundTag writeNBT(Capability<IKarma> capability, IKarma instance, Direction side) {
		CompoundTag entry = new CompoundTag();
		entry.putBoolean("Active", instance.isActive());
		entry.putFloat("Amount", instance.getKarma());
		return entry;
	}
}
