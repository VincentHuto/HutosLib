package com.vincenthuto.hutoslib.common.block.entity;

import javax.annotation.Nonnull;

import com.vincenthuto.hutoslib.client.particle.factory.GlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.util.HLParticleUtils;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DisplayPedestalBlockEntity extends TileSimpleInventory {

	public DisplayPedestalBlockEntity(BlockPos pos, BlockState state) {
		super(HLBlockEntityInit.display_pedestal.get(), pos, state);
	}

	public static void animTick(Level level, BlockPos pos, BlockState state, DisplayPedestalBlockEntity ent) {
		if (level.isClientSide) {
			int globalPartCount = 128;
			Vec3[] fibboSphere = HLParticleUtils.randomSphere(globalPartCount, -level.getGameTime() * 0.01, 0.5);
			Vec3[] corona = HLParticleUtils.randomSphere(globalPartCount, -level.getGameTime() * 0.01, 0.55);
			Vec3[] inversedSphere = HLParticleUtils.inversedSphere(globalPartCount, -level.getGameTime() * 0.01, 0.5,
					false);
			Vec3[] earth = HLParticleUtils.randomSphere(globalPartCount, -level.getGameTime() * 0.01, 0.1);
			Vec3[] mars = HLParticleUtils.randomSphere(globalPartCount, -level.getGameTime() * 0.01, 0.08);

			Vec3[] randomSwim = HLParticleUtils.randomSwimming(globalPartCount, -level.getGameTime() * 0.005, 1, false);
			Vec3[] lotusFountain = HLParticleUtils.implode(globalPartCount, -level.getGameTime() * 0.005, 1, true);

			for (int i = 0; i < globalPartCount; i++) {

//				level.addParticle(
//						GlowParticleFactory.createData(new ParticleColor((int) (fibboSphere[i].x * 255),
//								(int) (fibboSphere[i].y * 255), (int) (fibboSphere[i].z * 255))),
//						pos.getX() + fibboSphere[i].x + .5, pos.getY() + 1.5 + fibboSphere[i].y,
//						pos.getZ() + fibboSphere[i].z + .5, 0, 0.00, 0);
//
//				level.addParticle(
//						GlowParticleFactory.createData(new ParticleColor((int) (inversedSphere[i].x * 255),
//								(int) (inversedSphere[i].y * 255), (int) (inversedSphere[i].z * 255))),
//						pos.getX() + inversedSphere[i].x + .5, pos.getY() + 1.5 + inversedSphere[i].y,
//						pos.getZ() + inversedSphere[i].z + .5, 0, 0.00, 0);

				level.addParticle(
						GlowParticleFactory.createData(new ParticleColor((int) (lotusFountain[i].x * 255),
								(int) (lotusFountain[i].y * 255), (int) (lotusFountain[i].z * 255))),
						pos.getX() + lotusFountain[i].x + .5, pos.getY() + 1.1 + lotusFountain[i].y+1,
						pos.getZ() + lotusFountain[i].z + .5, 0, 0.00, 0);

				/*
				 * // This creates a Star like effect
				 * world.addParticle(GlowParticleData.createData(new ParticleColor(255,
				 * (world.rand.nextInt()), 0)), pos.getX() + fibboSphere[i].x + .5, pos.getY() +
				 * 1.5 + fibboSphere[i].y, pos.getZ() + fibboSphere[i].z + .5, 0, 0.00, 0);
				 * 
				 * if (i % 2 == 0) { world.addParticle(GlowParticleData.createData(new
				 * ParticleColor(100, 80, 10)), pos.getX() + corona[i].x + .5, pos.getY() + 1.5
				 * + corona[i].y, pos.getZ() + corona[i].z + .5, 0.0, -0.00, 0.0); }
				 * world.addParticle(GlowParticleData.createData(new ParticleColor(255, 0, 0)),
				 * pos.getX() + inversedSphere[i].x + .5, pos.getY() + 1.5 +
				 * inversedSphere[i].y, pos.getZ() + inversedSphere[i].z + .5, 0, 0.00, 0);
				 */
			}
		}
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		return super.save(compound);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		super.getUpdatePacket();
		CompoundTag nbtTag = new CompoundTag();
		nbtTag.merge(itemHandler.serializeNBT());
		return ClientboundBlockEntityDataPacket.create(this);
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
