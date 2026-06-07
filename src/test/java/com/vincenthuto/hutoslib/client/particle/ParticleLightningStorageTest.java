package com.vincenthuto.hutoslib.client.particle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hutoslib.math.Vector3;

class ParticleLightningStorageTest {

	@Test
	void constructorUsesConfiguredMaxOffset() throws ReflectiveOperationException {
		ParticleLightningStorage storage = new ParticleLightningStorage(new Vector3(0, 0, 0), new Vector3(4, 0, 0), 1L,
				1.0F, 10, 3, 0.5F);

		Field field = ParticleLightningStorage.class.getDeclaredField("maxOffset");
		field.setAccessible(true);

		assertEquals(0.5F, field.getFloat(storage));
	}
}
