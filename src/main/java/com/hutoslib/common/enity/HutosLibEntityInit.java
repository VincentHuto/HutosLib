package com.hutoslib.common.enity;

import com.hutoslib.HutosLib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Bus.MOD)
public class HutosLibEntityInit {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			HutosLib.MOD_ID);

	public static final RegistryObject<EntityType<TestMobEntity>> testmob = ENTITY_TYPES.register("testmob",
			() -> EntityType.Builder.<TestMobEntity>of(TestMobEntity::new, MobCategory.MONSTER).sized(0.9f, 1.3f)
					.build(new ResourceLocation(HutosLib.MOD_ID, "testmob").toString()));

	
	 @SubscribeEvent
	    public static void attribs(EntityAttributeCreationEvent e) {
	        e.put(HutosLibEntityInit.testmob.get(), TestMobEntity.createAttributes().build());
	    }

}
