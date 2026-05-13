package com.vincenthuto.hutoslib.client.particle.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.common.util.ParticleColor;
import com.vincenthuto.hutoslib.common.registry.HLParticleInit;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


/**
 * Simplified verison of ElementalCraft
 * https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class DarkColorParticleData implements ParticleOptions {

	public static final MapCodec<DarkColorParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, DarkColorParticleData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DarkColorParticleData> STREAM_CODEC =
			ByteBufCodecs.STRING_UTF8.map(
					s -> new DarkColorParticleData(ParticleColor.deserialize(s)),
					d -> d.color.serialize()
			).cast();

	private ParticleType<DarkColorParticleData> type;
	public ParticleColor color;

	public DarkColorParticleData(float r, float g, float b) {
		this.color = new ParticleColor(r, g, b);
		this.type = HLParticleInit.dark_glow.get();
	}

	public DarkColorParticleData(ParticleColor color) {
		this.color = color;
		this.type = HLParticleInit.dark_glow.get();
	}

	public DarkColorParticleData(ParticleType<DarkColorParticleData> particleTypeData, ParticleColor color) {
		this.type = particleTypeData;
		this.color = color;
	}

	@Override
	public ParticleType<DarkColorParticleData> getType() {
		return type;
	}
}

