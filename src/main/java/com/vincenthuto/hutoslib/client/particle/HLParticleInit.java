package com.vincenthuto.hutoslib.client.particle;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.data.ColorLightningData;
import com.vincenthuto.hutoslib.client.particle.data.ColorParticleData;
import com.vincenthuto.hutoslib.client.particle.data.DarkColorParticleData;
import com.vincenthuto.hutoslib.client.particle.data.EmberParticleData;
import com.vincenthuto.hutoslib.client.particle.factory.DarkGlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.EmberParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.GlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.type.DarkGlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.EmberParticleType;
import com.vincenthuto.hutoslib.client.particle.type.GlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.LightningParticleType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HLParticleInit {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
			.create(ForgeRegistries.PARTICLE_TYPES, HutosLib.MOD_ID);

	public static final RegistryObject<ParticleType<ColorParticleData>> glow = PARTICLE_TYPES.register("glow",
			() -> new GlowParticleType());

	public static final RegistryObject<ParticleType<DarkColorParticleData>> dark_glow = PARTICLE_TYPES
			.register("dark_glow", () -> new DarkGlowParticleType());

	public static RegistryObject<ParticleType<ColorLightningData>> lightning_bolt = PARTICLE_TYPES
			.register("lightning_bolt", () -> new LightningParticleType());

	public static final RegistryObject<ParticleType<EmberParticleData>> ember = PARTICLE_TYPES.register("ember",
			() -> new EmberParticleType());

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent  event) {
		Minecraft.getInstance().particleEngine.register(glow.get(), GlowParticleFactory::new);
		Minecraft.getInstance().particleEngine.register(dark_glow.get(), DarkGlowParticleFactory::new);
		Minecraft.getInstance().particleEngine.register(lightning_bolt.get(), LightningParticleFactory::new);
		Minecraft.getInstance().particleEngine.register(ember.get(), EmberParticleFactory::new);

	}

}
