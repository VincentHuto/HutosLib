package com.vincenthuto.hutoslib.common.block.entity;

import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.lightning.LightningTesterSpawner;

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

public class LightningTesterBlockEntity extends BlockEntity {
	private static final String CONFIG_KEY = "LightningTestConfig";

	private LightningTestConfig config = LightningTestConfig.defaults();
	private int repeatTicks;

	public LightningTesterBlockEntity(BlockPos pos, BlockState state) {
		super(HLBlockEntityInit.lightning_tester.get(), pos, state);
	}

	public LightningTestConfig getConfig() {
		return config;
	}

	public void setConfig(LightningTestConfig config) {
		this.config = config.clamped();
		this.repeatTicks = 0;
		setChanged();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, LightningTesterBlockEntity blockEntity) {
		LightningTestConfig config = blockEntity.config;
		if (!config.repeat() || !(level instanceof ServerLevel serverLevel)) {
			return;
		}
		blockEntity.repeatTicks++;
		if (blockEntity.repeatTicks < config.repeatInterval()) {
			return;
		}
		blockEntity.repeatTicks = 0;
		Vec3 start = Vec3.atCenterOf(pos);
		LightningTesterSpawner.spawn(serverLevel, start, start.add(config.targetOffset()), config);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains(CONFIG_KEY)) {
			config = LightningTestConfig.fromTag(tag.getCompound(CONFIG_KEY));
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
