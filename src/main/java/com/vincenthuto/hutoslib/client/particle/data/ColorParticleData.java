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


public class ColorParticleData implements ParticleOptions {

	public static final MapCodec<ColorParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, ColorParticleData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ColorParticleData> STREAM_CODEC =
			ByteBufCodecs.STRING_UTF8.map(
					s -> new ColorParticleData(ParticleColor.deserialize(s)),
					d -> d.color.serialize()
			).cast();

	private ParticleType<ColorParticleData> type;
	public ParticleColor color;

	public ColorParticleData(float r, float g, float b) {
		this.color = new ParticleColor(r, g, b);
		this.type = HLParticleInit.glow.get();
	}

	public ColorParticleData(ParticleColor color) {
		this.color = color;
		this.type = HLParticleInit.glow.get();
	}

	public ColorParticleData(ParticleType<ColorParticleData> particleTypeData, ParticleColor color) {
		this.type = particleTypeData;
		this.color = color;
	}

	@Override
	public ParticleType<ColorParticleData> getType() {
		return type;
	}
}