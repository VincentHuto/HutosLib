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


public class EmberParticleData implements ParticleOptions {

	public static final MapCodec<EmberParticleData> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
					Codec.FLOAT.fieldOf("a").forGetter(d -> d.alpha),
					Codec.FLOAT.fieldOf("s").forGetter(d -> d.scale),
					Codec.INT.fieldOf("l").forGetter(d -> d.life)).apply(instance, EmberParticleData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EmberParticleData> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, d -> d.color.serialize(),
					ByteBufCodecs.FLOAT, d -> d.alpha,
					ByteBufCodecs.FLOAT, d -> d.scale,
					ByteBufCodecs.INT, d -> d.life,
					(s, a, sc, l) -> new EmberParticleData(ParticleColor.deserialize(s), a, sc, l));

	private ParticleType<EmberParticleData> type;
	public ParticleColor color;
	public float scale;
	public float alpha;
	public int life;

	public EmberParticleData(float r, float g, float b, float s, float a, int l) {
		this.color = new ParticleColor(r, g, b);
		this.type = HLParticleInit.ember.get();
		this.alpha = a;
		this.scale = s;
		this.life = l;
	}

	public EmberParticleData(ParticleColor color, float alpha, float scale, int life) {
		this.color = color;
		this.type = HLParticleInit.ember.get();
		this.alpha = alpha;
		this.scale = scale;
		this.life = life;
	}

	public EmberParticleData(ParticleType<EmberParticleData> particleTypeData, ParticleColor color, float scale,
			float alpha, int life) {
		this.type = particleTypeData;
		this.color = color;
		this.alpha = alpha;
		this.scale = scale;
		this.life = life;
	}

	@Override
	public ParticleType<EmberParticleData> getType() {
		return type;
	}

}