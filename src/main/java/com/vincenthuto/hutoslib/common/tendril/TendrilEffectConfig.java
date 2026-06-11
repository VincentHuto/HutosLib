package com.vincenthuto.hutoslib.common.tendril;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;

public record TendrilEffectConfig(Mode mode, int coreColor, int glowColor, float range, float targetOffsetX,
		float targetOffsetY, float targetOffsetZ, int growTicks, int holdTicks, int fadeTicks, int segments,
		int strandCount, float baseWidth, float tipScale, int branchCount, int branchDepth, float branchLength,
		float branchSpread, float writheAmplitude, float writheFrequency, float curl, float sag,
		float surfaceSnapDistance, float surfaceLift, boolean blendColors, boolean fixedSeed, long seed, boolean repeat,
		int repeatInterval) {

	private static final String ROOT_KEY = "hutoslib_tendril_tester";

	public enum Mode {
		FREEFORM, SURFACE;

		public static Mode byName(String name) {
			for (Mode mode : values()) {
				if (mode.name().equals(name)) {
					return mode;
				}
			}
			return FREEFORM;
		}
	}

	public static TendrilEffectConfig defaults() {
		return new TendrilEffectConfig(Mode.FREEFORM, 0xDD10070A, 0x88B70B19, 16.0F, 0.0F, 2.0F, 0.0F, 10,
				10, 10, 16, 1, 0.12F, 0.12F, 3, 2, 0.35F, 0.8F, 0.14F, 0.06F, 0.7F, 0.15F, 2.0F, 0.12F,
				true, false, 0L, false, 20);
	}

	public static TendrilEffectConfig fromBuffer(FriendlyByteBuf buf) {
		return new TendrilEffectConfig(Mode.byName(buf.readUtf()), buf.readInt(), buf.readInt(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt(), buf.readInt(),
				buf.readInt(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt(),
				buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readLong(), buf.readBoolean(),
				buf.readInt()).clamped();
	}

	public static TendrilEffectConfig fromItem(ItemStack stack) {
		CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = customData.copyTag();
		return root.contains(ROOT_KEY) ? fromTag(root.getCompound(ROOT_KEY)) : defaults();
	}

	public static TendrilEffectConfig fromTag(CompoundTag tag) {
		TendrilEffectConfig defaults = defaults();
		return new TendrilEffectConfig(tag.contains("mode") ? Mode.byName(tag.getString("mode")) : defaults.mode(),
				tag.contains("coreColor") ? tag.getInt("coreColor") : defaults.coreColor(),
				tag.contains("glowColor") ? tag.getInt("glowColor") : defaults.glowColor(),
				tag.contains("range") ? tag.getFloat("range") : defaults.range(),
				tag.contains("targetOffsetX") ? tag.getFloat("targetOffsetX") : defaults.targetOffsetX(),
				tag.contains("targetOffsetY") ? tag.getFloat("targetOffsetY") : defaults.targetOffsetY(),
				tag.contains("targetOffsetZ") ? tag.getFloat("targetOffsetZ") : defaults.targetOffsetZ(),
				tag.contains("growTicks") ? tag.getInt("growTicks") : defaults.growTicks(),
				tag.contains("holdTicks") ? tag.getInt("holdTicks") : defaults.holdTicks(),
				tag.contains("fadeTicks") ? tag.getInt("fadeTicks") : defaults.fadeTicks(),
				tag.contains("segments") ? tag.getInt("segments") : defaults.segments(),
				tag.contains("strandCount") ? tag.getInt("strandCount") : defaults.strandCount(),
				tag.contains("baseWidth") ? tag.getFloat("baseWidth") : defaults.baseWidth(),
				tag.contains("tipScale") ? tag.getFloat("tipScale") : defaults.tipScale(),
				tag.contains("branchCount") ? tag.getInt("branchCount") : defaults.branchCount(),
				tag.contains("branchDepth") ? tag.getInt("branchDepth") : defaults.branchDepth(),
				tag.contains("branchLength") ? tag.getFloat("branchLength") : defaults.branchLength(),
				tag.contains("branchSpread") ? tag.getFloat("branchSpread") : defaults.branchSpread(),
				tag.contains("writheAmplitude") ? tag.getFloat("writheAmplitude") : defaults.writheAmplitude(),
				tag.contains("writheFrequency") ? tag.getFloat("writheFrequency") : defaults.writheFrequency(),
				tag.contains("curl") ? tag.getFloat("curl") : defaults.curl(),
				tag.contains("sag") ? tag.getFloat("sag") : defaults.sag(),
				tag.contains("surfaceSnapDistance") ? tag.getFloat("surfaceSnapDistance")
						: defaults.surfaceSnapDistance(),
				tag.contains("surfaceLift") ? tag.getFloat("surfaceLift") : defaults.surfaceLift(),
				tag.contains("blendColors") ? tag.getBoolean("blendColors") : defaults.blendColors(),
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

	public TendrilEffectConfig clamped() {
		return new TendrilEffectConfig(mode == null ? Mode.FREEFORM : mode, coreColor, glowColor,
				clamp(range, 1.0F, 128.0F), clamp(targetOffsetX, -64.0F, 64.0F),
				clamp(targetOffsetY, -64.0F, 64.0F), clamp(targetOffsetZ, -64.0F, 64.0F),
				clamp(growTicks, 1, 200), clamp(holdTicks, 1, 200), clamp(fadeTicks, 1, 200),
				clamp(segments, 2, 64), clamp(strandCount, 1, 8), clamp(baseWidth, 0.01F, 2.0F),
				clamp(tipScale, 0.01F, 1.0F), clamp(branchCount, 0, 16), clamp(branchDepth, 0, 4),
				clamp(branchLength, 0.05F, 1.0F), clamp(branchSpread, 0.0F, 3.14159F),
				clamp(writheAmplitude, 0.0F, 2.0F), clamp(writheFrequency, 0.001F, 1.0F),
				clamp(curl, -4.0F, 4.0F), clamp(sag, -4.0F, 4.0F),
				clamp(surfaceSnapDistance, 0.0F, 8.0F), clamp(surfaceLift, 0.0F, 1.0F), blendColors, fixedSeed, seed,
				repeat, clamp(repeatInterval, 5, 200));
	}

	public int totalLifetime() {
		TendrilEffectConfig config = clamped();
		return config.growTicks() + config.holdTicks() + config.fadeTicks();
	}

	public Vec3 targetOffset() {
		return new Vec3(targetOffsetX, targetOffsetY, targetOffsetZ);
	}

	public CompoundTag toTag() {
		TendrilEffectConfig config = clamped();
		CompoundTag tag = new CompoundTag();
		tag.putString("mode", config.mode().name());
		tag.putInt("coreColor", config.coreColor());
		tag.putInt("glowColor", config.glowColor());
		tag.putFloat("range", config.range());
		tag.putFloat("targetOffsetX", config.targetOffsetX());
		tag.putFloat("targetOffsetY", config.targetOffsetY());
		tag.putFloat("targetOffsetZ", config.targetOffsetZ());
		tag.putInt("growTicks", config.growTicks());
		tag.putInt("holdTicks", config.holdTicks());
		tag.putInt("fadeTicks", config.fadeTicks());
		tag.putInt("segments", config.segments());
		tag.putInt("strandCount", config.strandCount());
		tag.putFloat("baseWidth", config.baseWidth());
		tag.putFloat("tipScale", config.tipScale());
		tag.putInt("branchCount", config.branchCount());
		tag.putInt("branchDepth", config.branchDepth());
		tag.putFloat("branchLength", config.branchLength());
		tag.putFloat("branchSpread", config.branchSpread());
		tag.putFloat("writheAmplitude", config.writheAmplitude());
		tag.putFloat("writheFrequency", config.writheFrequency());
		tag.putFloat("curl", config.curl());
		tag.putFloat("sag", config.sag());
		tag.putFloat("surfaceSnapDistance", config.surfaceSnapDistance());
		tag.putFloat("surfaceLift", config.surfaceLift());
		tag.putBoolean("blendColors", config.blendColors());
		tag.putBoolean("fixedSeed", config.fixedSeed());
		tag.putLong("seed", config.seed());
		tag.putBoolean("repeat", config.repeat());
		tag.putInt("repeatInterval", config.repeatInterval());
		return tag;
	}

	public void toBuffer(FriendlyByteBuf buf) {
		TendrilEffectConfig config = clamped();
		buf.writeUtf(config.mode().name());
		buf.writeInt(config.coreColor());
		buf.writeInt(config.glowColor());
		buf.writeFloat(config.range());
		buf.writeFloat(config.targetOffsetX());
		buf.writeFloat(config.targetOffsetY());
		buf.writeFloat(config.targetOffsetZ());
		buf.writeInt(config.growTicks());
		buf.writeInt(config.holdTicks());
		buf.writeInt(config.fadeTicks());
		buf.writeInt(config.segments());
		buf.writeInt(config.strandCount());
		buf.writeFloat(config.baseWidth());
		buf.writeFloat(config.tipScale());
		buf.writeInt(config.branchCount());
		buf.writeInt(config.branchDepth());
		buf.writeFloat(config.branchLength());
		buf.writeFloat(config.branchSpread());
		buf.writeFloat(config.writheAmplitude());
		buf.writeFloat(config.writheFrequency());
		buf.writeFloat(config.curl());
		buf.writeFloat(config.sag());
		buf.writeFloat(config.surfaceSnapDistance());
		buf.writeFloat(config.surfaceLift());
		buf.writeBoolean(config.blendColors());
		buf.writeBoolean(config.fixedSeed());
		buf.writeLong(config.seed());
		buf.writeBoolean(config.repeat());
		buf.writeInt(config.repeatInterval());
	}

	public void writeToItem(ItemStack stack) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(ROOT_KEY, toTag()));
	}

	public TendrilEffectConfig withBranching(int branchCount, int branchDepth, float branchLength,
			float branchSpread) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withColors(int coreColor, int glowColor) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withBlendColors(boolean blendColors) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withFixedSeed(boolean fixedSeed, long seed) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withLifecycle(int growTicks, int holdTicks, int fadeTicks) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withMode(Mode mode) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withRange(float range) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withRepeat(boolean repeat, int repeatInterval) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withShape(int segments, int strandCount, float baseWidth, float tipScale) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withSurface(float surfaceSnapDistance, float surfaceLift) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withTargetOffset(float x, float y, float z) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, x, y, z, growTicks, holdTicks, fadeTicks,
				segments, strandCount, baseWidth, tipScale, branchCount, branchDepth, branchLength, branchSpread,
				writheAmplitude, writheFrequency, curl, sag, surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed,
				repeat, repeatInterval).clamped();
	}

	public TendrilEffectConfig withWrithe(float writheAmplitude, float writheFrequency, float curl, float sag) {
		return new TendrilEffectConfig(mode, coreColor, glowColor, range, targetOffsetX, targetOffsetY,
				targetOffsetZ, growTicks, holdTicks, fadeTicks, segments, strandCount, baseWidth, tipScale,
				branchCount, branchDepth, branchLength, branchSpread, writheAmplitude, writheFrequency, curl, sag,
				surfaceSnapDistance, surfaceLift, blendColors, fixedSeed, seed, repeat, repeatInterval).clamped();
	}
}
