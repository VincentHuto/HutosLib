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

public class ColorLightningData implements ParticleOptions {

	private ParticleType<ColorLightningData> type;
	public static final Codec<ColorLightningData> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(Codec.FLOAT.fieldOf("r").forGetter(d -> d.color.getRed()),
					Codec.FLOAT.fieldOf("g").forGetter(d -> d.color.getGreen()),
					Codec.FLOAT.fieldOf("b").forGetter(d -> d.color.getBlue()),
					Codec.FLOAT.fieldOf("s").forGetter(d -> d.speed), Codec.INT.fieldOf("a").forGetter(d -> d.maxAge),
					Codec.INT.fieldOf("f").forGetter(d -> d.fract),
					Codec.FLOAT.fieldOf("o").forGetter(d -> d.maxOffset)).apply(instance, ColorLightningData::new));

	public ParticleColor color;
	public float speed;
	public int maxAge, fract;
	public float maxOffset;

	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<ColorLightningData> DESERIALIZER = new ParticleOptions.Deserializer<ColorLightningData>() {
		@Override
		public ColorLightningData fromCommand(ParticleType<ColorLightningData> type, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			return new ColorLightningData(type, ParticleColor.deserialize(reader.readString()), reader.readInt(),
					reader.readInt(), reader.readInt(), reader.readFloat());
		}

		@Override
		public ColorLightningData fromNetwork(ParticleType<ColorLightningData> type, FriendlyByteBuf buffer) {
			return new ColorLightningData(type, ParticleColor.deserialize(buffer.readUtf()), buffer.readInt(),
					buffer.readInt(), buffer.readInt(), buffer.readFloat());
		}
	};

	public ColorLightningData(float r, float g, float b, float s, int a, int f, float o) {
		this.color = new ParticleColor(r, g, b);
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

	@Override
	public void writeToNetwork(FriendlyByteBuf packetBuffer) {
		packetBuffer.writeUtf(color.serialize());
		packetBuffer.writeFloat(speed);
		packetBuffer.writeInt(maxAge);
		packetBuffer.writeInt(fract);
		packetBuffer.writeFloat(maxOffset);

	}

	@Override
	public String writeToString() {
		return type.getRegistryName().toString() + " " + color.serialize() + " " + speed + " " + maxAge + " " + fract
				+ " " + maxOffset;
	}

}