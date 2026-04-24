package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.factory.DarkGlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.EmberParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.GlowParticleFactory;
import com.vincenthuto.hutoslib.client.particle.factory.LightningParticleFactory;
import com.vincenthuto.hutoslib.client.particle.type.DarkGlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.EmberParticleType;
import com.vincenthuto.hutoslib.client.particle.type.GlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.LightningParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HLParticleInit {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
			.create(Registries.PARTICLE_TYPE, HutosLib.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, GlowParticleType> glow = PARTICLE_TYPES.register("glow",
			() -> new GlowParticleType());

	public static final DeferredHolder<ParticleType<?>, DarkGlowParticleType> dark_glow = PARTICLE_TYPES
			.register("dark_glow", () -> new DarkGlowParticleType());

	public static DeferredHolder<ParticleType<?>, LightningParticleType> lightning_bolt = PARTICLE_TYPES
			.register("lightning_bolt", () -> new LightningParticleType());

	public static final DeferredHolder<ParticleType<?>, EmberParticleType> ember = PARTICLE_TYPES.register("ember",
			() -> new EmberParticleType());

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent  event) {
		event.registerSpriteSet(glow.get(), GlowParticleFactory::new);
		event.registerSpriteSet(dark_glow.get(), DarkGlowParticleFactory::new);
		event.registerSpriteSet(lightning_bolt.get(), LightningParticleFactory::new);
		event.registerSpriteSet(ember.get(), EmberParticleFactory::new);

	}

}
