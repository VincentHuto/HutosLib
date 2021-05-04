package com.hutoslib.client.particles;

import com.hutoslib.HutosLib;
import com.hutoslib.client.particles.data.ColorLightningData;
import com.hutoslib.client.particles.data.ColorParticleData;
import com.hutoslib.client.particles.data.DarkColorParticleData;
import com.hutoslib.client.particles.factory.DarkGlowParticleFactory;
import com.hutoslib.client.particles.factory.GlowParticleFactory;
import com.hutoslib.client.particles.factory.ParticleLightningFactory;
import com.hutoslib.client.particles.type.DarkGlowParticleType;
import com.hutoslib.client.particles.type.GlowParticleType;
import com.hutoslib.client.particles.type.LightningParticleType;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleInit {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
			.create(ForgeRegistries.PARTICLE_TYPES, HutosLib.MOD_ID);

	public static final RegistryObject<ParticleType<ColorParticleData>> glow = PARTICLE_TYPES.register("glow",
			() -> new GlowParticleType());

	public static final RegistryObject<ParticleType<DarkColorParticleData>> dark_glow = PARTICLE_TYPES
			.register("dark_glow", () -> new DarkGlowParticleType());

	public static RegistryObject<ParticleType<ColorLightningData>> lightning_bolt = PARTICLE_TYPES
			.register("lightning_bolt", () -> new LightningParticleType());

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(glow.get(), GlowParticleFactory::new);
		Minecraft.getInstance().particles.registerFactory(dark_glow.get(), DarkGlowParticleFactory::new);
		Minecraft.getInstance().particles.registerFactory(lightning_bolt.get(), ParticleLightningFactory::new);

	}

}
