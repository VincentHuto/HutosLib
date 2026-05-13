package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.particle.type.DarkGlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.EmberParticleType;
import com.vincenthuto.hutoslib.client.particle.type.GlowParticleType;
import com.vincenthuto.hutoslib.client.particle.type.LightningParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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

}
