package com.vincenthuto.hutoslib.common.particle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record GenericParticleTestConfig(Kind kind, Shape shape, int color, boolean randomColor, int count, float spread, float speed,
		float scale, float alpha, int life, float range, boolean repeat, int repeatInterval) {
	private static final String ROOT_KEY = "hutoslib_generic_particle_tester";

	public enum Kind {
		GLOW("Glow"), EMBER("Ember"), DARK_GLOW("Dark Glow");

		private final String label;

		Kind(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}

		public Kind next() {
			Kind[] values = values();
			return values[(ordinal() + 1) % values.length];
		}

		public Kind previous() {
			Kind[] values = values();
			return values[(ordinal() + values.length - 1) % values.length];
		}

		public static Kind byName(String name) {
			for (Kind kind : values()) {
				if (kind.name().equals(name)) {
					return kind;
				}
			}
			return GLOW;
		}
	}

	public enum Shape {
		BURST("Burst"), FIBONACCI_SPHERE("Fib Sphere"), RANDOM_SPHERE("Rand Sphere"),
		INVERSED_SPHERE("Inverse"), IMPLODE("Implode"), LOTUS_FOUNTAIN("Lotus"),
		BLOOMING_FLOWER("Bloom"), COSMIC_BIRTH("Cosmic"), COSMIC_BIRTH_INVERSE("Cosmic Inv"),
		SQUASH_STRETCH("Squash"), RANDOM_SWIMMING("Swim"), TANGENT_FUNNEL("Funnel");

		private final String label;

		Shape(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}

		public Shape next() {
			Shape[] values = values();
			return values[(ordinal() + 1) % values.length];
		}

		public Shape previous() {
			Shape[] values = values();
			return values[(ordinal() + values.length - 1) % values.length];
		}

		public static Shape byName(String name) {
			for (Shape shape : values()) {
				if (shape.name().equals(name)) {
					return shape;
				}
			}
			return BURST;
		}
	}

	public static GenericParticleTestConfig defaults() {
		return new GenericParticleTestConfig(Kind.GLOW, Shape.BURST, 0xFFFF19B4, false, 16, 0.35F, 0.02F, 0.35F, 0.8F,
				24, 12.0F, false, 20);
	}

	public static GenericParticleTestConfig fromBuffer(FriendlyByteBuf buf) {
		return new GenericParticleTestConfig(Kind.byName(buf.readUtf()), Shape.byName(buf.readUtf()), buf.readInt(),
				buf.readBoolean(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
				buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readInt()).clamped();
	}

	public static GenericParticleTestConfig fromItem(ItemStack stack) {
		CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = customData.copyTag();
		return root.contains(ROOT_KEY) ? fromTag(root.getCompound(ROOT_KEY)) : defaults();
	}

	public static GenericParticleTestConfig fromTag(CompoundTag tag) {
		GenericParticleTestConfig defaults = defaults();
		return new GenericParticleTestConfig(tag.contains("kind") ? Kind.byName(tag.getString("kind"))
				: defaults.kind(), tag.contains("shape") ? Shape.byName(tag.getString("shape")) : defaults.shape(),
				tag.contains("color") ? tag.getInt("color") : defaults.color(),
				tag.contains("randomColor") ? tag.getBoolean("randomColor") : defaults.randomColor(),
				tag.contains("count") ? tag.getInt("count") : defaults.count(),
				tag.contains("spread") ? tag.getFloat("spread") : defaults.spread(),
				tag.contains("speed") ? tag.getFloat("speed") : defaults.speed(),
				tag.contains("scale") ? tag.getFloat("scale") : defaults.scale(),
				tag.contains("alpha") ? tag.getFloat("alpha") : defaults.alpha(),
				tag.contains("life") ? tag.getInt("life") : defaults.life(),
				tag.contains("range") ? tag.getFloat("range") : defaults.range(),
				tag.contains("repeat") ? tag.getBoolean("repeat") : defaults.repeat(),
				tag.contains("repeatInterval") ? tag.getInt("repeatInterval") : defaults.repeatInterval()).clamped();
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public GenericParticleTestConfig clamped() {
		return new GenericParticleTestConfig(kind == null ? Kind.GLOW : kind, shape == null ? Shape.BURST : shape,
				color, randomColor, clamp(count, 1, 256), clamp(spread, 0.0F, 8.0F), clamp(speed, 0.0F, 2.0F),
				clamp(scale, 0.05F, 4.0F), clamp(alpha, 0.0F, 1.0F), clamp(life, 1, 400),
				clamp(range, 1.0F, 128.0F), repeat, clamp(repeatInterval, 1, 200));
	}

	public CompoundTag toTag() {
		GenericParticleTestConfig config = clamped();
		CompoundTag tag = new CompoundTag();
		tag.putString("kind", config.kind().name());
		tag.putString("shape", config.shape().name());
		tag.putInt("color", config.color());
		tag.putBoolean("randomColor", config.randomColor());
		tag.putInt("count", config.count());
		tag.putFloat("spread", config.spread());
		tag.putFloat("speed", config.speed());
		tag.putFloat("scale", config.scale());
		tag.putFloat("alpha", config.alpha());
		tag.putInt("life", config.life());
		tag.putFloat("range", config.range());
		tag.putBoolean("repeat", config.repeat());
		tag.putInt("repeatInterval", config.repeatInterval());
		return tag;
	}

	public void toBuffer(FriendlyByteBuf buf) {
		GenericParticleTestConfig config = clamped();
		buf.writeUtf(config.kind().name());
		buf.writeUtf(config.shape().name());
		buf.writeInt(config.color());
		buf.writeBoolean(config.randomColor());
		buf.writeInt(config.count());
		buf.writeFloat(config.spread());
		buf.writeFloat(config.speed());
		buf.writeFloat(config.scale());
		buf.writeFloat(config.alpha());
		buf.writeInt(config.life());
		buf.writeFloat(config.range());
		buf.writeBoolean(config.repeat());
		buf.writeInt(config.repeatInterval());
	}

	public void writeToItem(ItemStack stack) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(ROOT_KEY, toTag()));
	}

	public GenericParticleTestConfig withAlpha(float alpha) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withColor(int color) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withEmber(float alpha, float scale, int life) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withKind(Kind kind) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withParticleShape(Shape shape) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withRandomColor(boolean randomColor) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withRange(float range) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withRepeat(boolean repeat, int repeatInterval) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}

	public GenericParticleTestConfig withShape(int count, float spread, float speed) {
		return new GenericParticleTestConfig(kind, shape, color, randomColor, count, spread, speed, scale, alpha, life,
				range, repeat, repeatInterval).clamped();
	}
}
