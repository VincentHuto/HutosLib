package com.vincenthuto.hutoslib.common.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.common.lightning.LightningTestBoltFactory;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;

import net.minecraft.world.phys.Vec3;

class PacketSpawnLightningTestTest {

	@Test
	void testerBoltDataSpawnsImmediately() {
		var config = LightningTestConfig.defaults();
		var bolt = LightningTestBoltFactory.create(Vec3.ZERO, new Vec3(8, 0, 0), 123L, config.outerColor(),
				config.size(), config);

		var bounds = bolt.getSpawnFunction().getSpawnDelayBounds(new java.util.Random(1L));

		assertEquals(0.0F, bounds.getLeft());
		assertEquals(0.0F, bounds.getRight());
	}

	@Test
	void testerBoltDataUsesSharedConfigControls() {
		var config = new LightningTestConfig(LightningTestConfig.Backend.BOLT, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
				16.0F, 0.0F, 2.0F, 0.0F, 20.0F, 2.0F, 80, 4, 0.6F, 0.25F, false, 0L, false, 20);

		var bolt = LightningTestBoltFactory.create(Vec3.ZERO, new Vec3(1, 0, 0), 123L, config.outerColor(),
				config.size(), config);

		assertEquals(4, bolt.getSegments());
		assertEquals(0.25F, bolt.getSize());
		assertEquals(10, bolt.getLifespan());
		assertEquals(0.6F, bolt.getRenderInfo().getSpreadFactor());
	}
}
