package com.vincenthuto.hutoslib.common.lightning;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;

public record LightningTestConfig(Backend backend, int colorPreset, int outerColor, int innerColor, float range,
		float targetOffsetX, float targetOffsetY, float targetOffsetZ, float ticksPerMeter, float speed, int maxAge,
		int fract, float maxOffset, float size, boolean fixedSeed, long seed, boolean repeat, int repeatInterval) {

	private static final String ROOT_KEY = "hutoslib_lightning_tester";

	public enum Backend {
		BOLT, PARTICLE;

		static Backend byName(String name) {
			for (Backend backend : values()) {
				if (backend.name().equals(name)) {
					return backend;
				}
			}
			return BOLT;
		}
	}

	public static LightningTestConfig defaults() {
		return new LightningTestConfig(Backend.BOLT, 0xFF00FFFF, 0xFF00FFFF, 0xFFFFFFFF, 16.0F, 0.0F, 2.0F, 0.0F,
				64.0F, 1.0F, 10, 9, 0.2F, 0.08F, false, 0L, false, 20);
	}

	public static LightningTestConfig fromBuffer(FriendlyByteBuf buf) {
		return new LightningTestConfig(Backend.byName(buf.readUtf()), buf.readInt(), buf.readInt(), buf.readInt(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readInt(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readLong(),
				buf.readBoolean(), buf.readInt()).clamped();
	}

	public static LightningTestConfig fromItem(ItemStack stack) {
		CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = customData.copyTag();
		return root.contains(ROOT_KEY) ? fromTag(root.getCompound(ROOT_KEY)) : defaults();
	}

	public static LightningTestConfig fromTag(CompoundTag tag) {
		LightningTestConfig defaults = defaults();
		return new LightningTestConfig(tag.contains("backend") ? Backend.byName(tag.getString("backend"))
				: defaults.backend(), tag.contains("colorPreset") ? tag.getInt("colorPreset") : defaults.colorPreset(),
				tag.contains("outerColor") ? tag.getInt("outerColor") : defaults.outerColor(),
				tag.contains("innerColor") ? tag.getInt("innerColor") : defaults.innerColor(),
				tag.contains("range") ? tag.getFloat("range") : defaults.range(),
				tag.contains("targetOffsetX") ? tag.getFloat("targetOffsetX") : defaults.targetOffsetX(),
				tag.contains("targetOffsetY") ? tag.getFloat("targetOffsetY") : defaults.targetOffsetY(),
				tag.contains("targetOffsetZ") ? tag.getFloat("targetOffsetZ") : defaults.targetOffsetZ(),
				tag.contains("ticksPerMeter") ? tag.getFloat("ticksPerMeter") : defaults.ticksPerMeter(),
				tag.contains("speed") ? tag.getFloat("speed") : defaults.speed(),
				tag.contains("maxAge") ? tag.getInt("maxAge") : defaults.maxAge(),
				tag.contains("fract") ? tag.getInt("fract") : defaults.fract(),
				tag.contains("maxOffset") ? tag.getFloat("maxOffset") : defaults.maxOffset(),
				tag.contains("size") ? tag.getFloat("size") : defaults.size(),
				tag.contains("fixedSeed") ? tag.getBoolean("fixedSeed") : defaults.fixedSeed(),
				tag.contains("seed") ? tag.getLong("seed") : defaults.seed(),
				tag.contains("repeat") ? tag.getBoolean("repeat") : defaults.repeat(),
				tag.contains("repeatInterval") ? tag.getInt("repeatInterval") : defaults.repeatInterval()).clamped();
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public LightningTestConfig clamped() {
		return new LightningTestConfig(backend == null ? Backend.BOLT : backend, colorPreset, outerColor, innerColor,
				clamp(range, 1.0F, 128.0F), clamp(targetOffsetX, -64.0F, 64.0F), clamp(targetOffsetY, -64.0F, 64.0F),
				clamp(targetOffsetZ, -64.0F, 64.0F), clamp(ticksPerMeter, 1.0F, 128.0F), clamp(speed, 0.05F, 20.0F),
				clamp(maxAge, 1, 80), clamp(fract, 1, 12), clamp(maxOffset, 0.01F, 4.0F), clamp(size, 0.01F, 1.0F),
				fixedSeed, seed, repeat, clamp(repeatInterval, 5, 200));
	}

	public Vec3 targetOffset() {
		return new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
	}

	public CompoundTag toTag() {
		LightningTestConfig config = clamped();
		CompoundTag tag = new CompoundTag();
		tag.putString("backend", config.backend().name());
		tag.putInt("colorPreset", config.colorPreset());
		tag.putInt("outerColor", config.outerColor());
		tag.putInt("innerColor", config.innerColor());
		tag.putFloat("range", config.range());
		tag.putFloat("targetOffsetX", config.targetOffsetX());
		tag.putFloat("targetOffsetY", config.targetOffsetY());
		tag.putFloat("targetOffsetZ", config.targetOffsetZ());
		tag.putFloat("ticksPerMeter", config.ticksPerMeter());
		tag.putFloat("speed", config.speed());
		tag.putInt("maxAge", config.maxAge());
		tag.putInt("fract", config.fract());
		tag.putFloat("maxOffset", config.maxOffset());
		tag.putFloat("size", config.size());
		tag.putBoolean("fixedSeed", config.fixedSeed());
		tag.putLong("seed", config.seed());
		tag.putBoolean("repeat", config.repeat());
		tag.putInt("repeatInterval", config.repeatInterval());
		return tag;
	}

	public void toBuffer(FriendlyByteBuf buf) {
		LightningTestConfig config = clamped();
		buf.writeUtf(config.backend().name());
		buf.writeInt(config.colorPreset());
		buf.writeInt(config.outerColor());
		buf.writeInt(config.innerColor());
		buf.writeFloat(config.range());
		buf.writeFloat(config.targetOffsetX());
		buf.writeFloat(config.targetOffsetY());
		buf.writeFloat(config.targetOffsetZ());
		buf.writeFloat(config.ticksPerMeter());
		buf.writeFloat(config.speed());
		buf.writeInt(config.maxAge());
		buf.writeInt(config.fract());
		buf.writeFloat(config.maxOffset());
		buf.writeFloat(config.size());
		buf.writeBoolean(config.fixedSeed());
		buf.writeLong(config.seed());
		buf.writeBoolean(config.repeat());
		buf.writeInt(config.repeatInterval());
	}

	public void writeToItem(ItemStack stack) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(ROOT_KEY, toTag()));
	}

	public LightningTestConfig withBackend(Backend backend) {
		return new LightningTestConfig(backend, colorPreset, outerColor, innerColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, ticksPerMeter, speed, maxAge, fract, maxOffset, size, fixedSeed, seed, repeat,
				repeatInterval).clamped();
	}

	public LightningTestConfig withColorPreset(int colorPreset) {
		return new LightningTestConfig(backend, colorPreset, colorPreset, innerColor, range, targetOffsetX,
				targetOffsetY, targetOffsetZ, ticksPerMeter, speed, maxAge, fract, maxOffset, size, fixedSeed, seed,
				repeat, repeatInterval).clamped();
	}

	public LightningTestConfig withFixedSeed(boolean fixedSeed, long seed) {
		return new LightningTestConfig(backend, colorPreset, outerColor, innerColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, ticksPerMeter, speed, maxAge, fract, maxOffset, size, fixedSeed, seed, repeat,
				repeatInterval).clamped();
	}

	public LightningTestConfig withRepeat(boolean repeat, int repeatInterval) {
		return new LightningTestConfig(backend, colorPreset, outerColor, innerColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, ticksPerMeter, speed, maxAge, fract, maxOffset, size, fixedSeed, seed, repeat,
				repeatInterval).clamped();
	}

	public LightningTestConfig withTargetOffset(float x, float y, float z) {
		return new LightningTestConfig(backend, colorPreset, outerColor, innerColor, range, x, y, z, ticksPerMeter,
				speed, maxAge, fract, maxOffset, size, fixedSeed, seed, repeat, repeatInterval).clamped();
	}
}
