package com.vincenthuto.hutoslib.client.particle.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.particle.HLParticleInit;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Simplified verison of ElementalCraft
 * https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class DarkColorParticleData implements ParticleOptions {

	public static final Codec<DarkColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, DarkColorParticleData::new));
	public static final ParticleOptions.Deserializer<DarkColorParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {

		@Override
		public DarkColorParticleData fromCommand(ParticleType<DarkColorParticleData> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			return new DarkColorParticleData(type, ParticleColor.deserialize(reader.readString()));
		}

		@Override
		public DarkColorParticleData fromNetwork(ParticleType<DarkColorParticleData> type,
				FriendlyByteBuf p_123736_) {
			return new DarkColorParticleData(type, ParticleColor.deserialize(p_123736_.readUtf()));
		}
	};

	private  ParticleType<DarkColorParticleData> type;

	public ParticleColor color;

	public DarkColorParticleData(float r, float g, float b) {
		this.color = new ParticleColor(r, g, b);
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

	@Override
	public void writeToNetwork(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeUtf(color.serialize());
	}

	@Override
	public String writeToString() {
		return ForgeRegistries.PARTICLE_TYPES.getKey(type).toString() + " " + color.serialize();
	}

}