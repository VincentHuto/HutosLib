package com.vincenthuto.hutoslib.common.block.entity;

import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTesterSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GenericParticleTesterBlockEntity extends BlockEntity {
	private static final String CONFIG_KEY = "GenericParticleTestConfig";

	private GenericParticleTestConfig config = GenericParticleTestConfig.defaults();
	private int repeatTicks;

	public GenericParticleTesterBlockEntity(BlockPos pos, BlockState state) {
		super(HLBlockEntityInit.generic_particle_tester.get(), pos, state);
	}

	public GenericParticleTestConfig getConfig() {
		return config;
	}

	public void setConfig(GenericParticleTestConfig config) {
		this.config = config.clamped();
		this.repeatTicks = 0;
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state,
			GenericParticleTesterBlockEntity blockEntity) {
		GenericParticleTestConfig config = blockEntity.config;
		if (!config.repeat() || !(level instanceof ServerLevel serverLevel)) {
			return;
		}
		blockEntity.repeatTicks++;
		if (blockEntity.repeatTicks < config.repeatInterval()) {
			return;
		}
		blockEntity.repeatTicks = 0;
		GenericParticleTesterSpawner.spawn(serverLevel, spawnPos(pos), config);
	}

	public static Vec3 spawnPos(BlockPos pos) {
		return Vec3.atCenterOf(pos).add(0.0D, 0.75D, 0.0D);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains(CONFIG_KEY)) {
			config = GenericParticleTestConfig.fromTag(tag.getCompound(CONFIG_KEY));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put(CONFIG_KEY, config.toTag());
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveCustomOnly(registries);
	}
}
