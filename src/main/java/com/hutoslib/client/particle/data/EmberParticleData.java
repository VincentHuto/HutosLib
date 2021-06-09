package com.hutoslib.client.particle.data;

import com.hutoslib.client.particle.ParticleInit;
import com.hutoslib.client.particle.util.ParticleColor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

/**
 * Simplified verison of ElementalCraft
 * https://github.com/Sirttas/ElementalCraft/blob/b91ca42b3d139904d9754d882a595406bad1bd18/src/main/java/sirttas/elementalcraft/particle/ElementTypeParticleData.java
 */

public class EmberParticleData implements IParticleData {

	private ParticleType<EmberParticleData> type;
	public static final Codec<EmberParticleData> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
					Codec.FLOAT.fieldOf("a").forGetter(d -> d.alpha), Codec.FLOAT.fieldOf("s").forGetter(d -> d.scale),
					Codec.INT.fieldOf("l").forGetter(d -> d.life)).apply(instance, EmberParticleData::new));

	public ParticleColor color;
	public float scale;
	public float alpha;
	public int life;

	@SuppressWarnings("deprecation")
	public static final IParticleData.IDeserializer<EmberParticleData> DESERIALIZER = new IParticleData.IDeserializer<EmberParticleData>() {
		@Override
		public EmberParticleData deserialize(ParticleType<EmberParticleData> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			return new EmberParticleData(type, ParticleColor.deserialize(reader.readString()), reader.readFloat(),
					reader.readFloat(), reader.readInt());
		}

		@Override
		public EmberParticleData read(ParticleType<EmberParticleData> type, PacketBuffer buffer) {
			return new EmberParticleData(type, ParticleColor.deserialize(buffer.readString()), buffer.readFloat(),
					buffer.readFloat(), buffer.readInt());
		}
	};

	public EmberParticleData(float r, float g, float b, float s, float a, int l) {
		this.color = new ParticleColor(r, g, b);
		this.type = ParticleInit.ember.get();
		this.alpha = a;
		this.scale = s;
		this.life = l;
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

	@Override
	public void write(PacketBuffer packetBuffer) {
		packetBuffer.writeString(color.serialize());
		packetBuffer.writeFloat(alpha);
		packetBuffer.writeFloat(scale);
		packetBuffer.writeInt(life);
	}

	@Override
	public String getParameters() {
		return type.getRegistryName().toString() + " " + color.serialize() + " " + alpha + " " + scale + " " + life;
	}
}