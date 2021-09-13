package com.vincenthuto.hutoslib.client.particle.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vincenthuto.hutoslib.client.particle.HutosLibParticleInit;
import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Simplified verison of ElementalCraft
 * https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class ColorParticleData implements ParticleOptions {

	private ParticleType<ColorParticleData> type;
	public static final Codec<ColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()))
			.apply(instance, ColorParticleData::new));

	public ParticleColor color;

	public static final ParticleOptions.Deserializer<ColorParticleData> DESERIALIZER = new ParticleOptions.Deserializer<ColorParticleData>() {
		@Override
		public ColorParticleData fromCommand(ParticleType<ColorParticleData> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			return new ColorParticleData(type, ParticleColor.deserialize(reader.readString()));
		}

		@Override
		public ColorParticleData fromNetwork(ParticleType<ColorParticleData> type, FriendlyByteBuf buffer) {
			return new ColorParticleData(type, ParticleColor.deserialize(buffer.readUtf()));
		}
	};

	public ColorParticleData(float r, float g, float b) {
		this.color = new ParticleColor(r, g, b);
		this.type = HutosLibParticleInit.glow.get();
	}

	public ColorParticleData(ParticleType<ColorParticleData> particleTypeData, ParticleColor color) {
		this.type = particleTypeData;
		this.color = color;
	}

	@Override
	public ParticleType<ColorParticleData> getType() {
		return type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeUtf(color.serialize());
	}

	@Override
	public String writeToString() {
		return type.getRegistryName().toString() + " " + color.serialize();
	}
}