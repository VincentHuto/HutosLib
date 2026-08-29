package com.vincenthuto.hutoslib.common.effectsource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.vincenthuto.hemomancy.viewer.HemomancyEffectCaller;

class EffectSourceInferenceTest {

	@Test
	void selectsFirstCallerOutsideEffectPlumbing() {
		var frames = List.of(
				new EffectSourceInference.Frame("com.vincenthuto.hutoslib.common.effectsource.EffectSourceTracker", "capture", "EffectSourceTracker.java", 40),
				new EffectSourceInference.Frame("net.neoforged.bus.impl.EventBus", "post", "EventBus.java", 300),
				new EffectSourceInference.Frame("net.minecraft.world.entity.LivingEntity", "addEffect", "LivingEntity.java", 977),
				new EffectSourceInference.Frame("example.magic.item.MoonPotionItem", "finishUsingItem", "MoonPotionItem.java", 73));

		assertEquals(frames.get(3), EffectSourceInference.selectCaller(frames).orElseThrow());
	}

	@Test
	void keepsVanillaGameplayCaller() {
		var frame = new EffectSourceInference.Frame("net.minecraft.world.item.PotionItem", "finishUsingItem", "PotionItem.java", 55);

		assertEquals(frame, EffectSourceInference.selectCaller(List.of(frame)).orElseThrow());
	}

	@Test
	void skipsReflectionFramesBeforeGameplayCaller() {
		var caller = new EffectSourceInference.Frame("example.magic.MoonBeacon", "applyAura", "MoonBeacon.java", 91);
		var frames = List.of(
				new EffectSourceInference.Frame("jdk.internal.reflect.DirectMethodHandleAccessor", "invoke", "DirectMethodHandleAccessor.java", 103),
				new EffectSourceInference.Frame("java.lang.reflect.Method", "invoke", "Method.java", 580), caller);

		assertEquals(caller, EffectSourceInference.selectCaller(frames).orElseThrow());
	}

	@Test
	void registeredPackageOwnerAttributesDevelopmentCallerToMod() {
		EffectSourceInference.registerPackageOwner("com.vincenthuto.", "broad_owner");
		EffectSourceInference.registerPackageOwner("com.vincenthuto.hemomancy.", "hemomancy");

		var caller = HemomancyEffectCaller.capture();

		assertEquals(HemomancyEffectCaller.class.getName(), caller.frame().className());
		assertEquals("hemomancy", caller.origin().modId());
	}

	@Test
	void packageOwnerRegistrationRejectsUnsafePrefixesAndBlankModIds() {
		assertThrows(IllegalArgumentException.class,
				() -> EffectSourceInference.registerPackageOwner("", "hemomancy"));
		assertThrows(IllegalArgumentException.class,
				() -> EffectSourceInference.registerPackageOwner("com.vincenthuto.hemomancy", "hemomancy"));
		assertThrows(IllegalArgumentException.class,
				() -> EffectSourceInference.registerPackageOwner("com.vincenthuto.hemomancy.", ""));
	}
}
