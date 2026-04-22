package com.vincenthuto.hutoslib.client.particle.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ColorLightningData implements ParticleOptions {

	public static final MapCodec<ColorLightningData> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
					Codec.FLOAT.fieldOf("s").forGetter(d -> d.speed),
					Codec.INT.fieldOf("a").forGetter(d -> d.maxAge),
					Codec.INT.fieldOf("f").forGetter(d -> d.fract),
					Codec.FLOAT.fieldOf("o").forGetter(d -> d.maxOffset)).apply(instance, ColorLightningData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ColorLightningData> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, d -> d.color.serialize(),
					ByteBufCodecs.FLOAT, d -> d.speed,
					ByteBufCodecs.INT, d -> d.maxAge,
					ByteBufCodecs.INT, d -> d.fract,
					ByteBufCodecs.FLOAT, d -> d.maxOffset,
					(s, sp, a, f, o) -> new ColorLightningData(ParticleColor.deserialize(s), sp, a, f, o));

	private ParticleType<ColorLightningData> type;
	public ParticleColor color;
	public float speed;
	public int maxAge, fract;
	public float maxOffset;

	public ColorLightningData(float r, float g, float b, float s, int a, int f, float o) {
		this.color = new ParticleColor(r, g, b);
		this.type = HLParticleInit.lightning_bolt.get();
		this.speed = s;
		this.maxAge = a;
		this.fract = f;
		this.maxOffset = o;
	}

	public ColorLightningData(ParticleColor color, float s, int a, int f, float o) {
		this.color = color;
		this.type = HLParticleInit.lightning_bolt.get();
		this.speed = s;
		this.maxAge = a;
		this.fract = f;
		this.maxOffset = o;
	}

	public ColorLightningData(ParticleType<ColorLightningData> particleTypeData, ParticleColor color, float s, int a,
			int f, float o) {
		this.type = particleTypeData;
		this.color = color;
		this.speed = s;
		this.maxAge = a;
		this.fract = f;
		this.maxOffset = o;
	}

	@Override
	public ParticleType<ColorLightningData> getType() {
		return type;
	}

}