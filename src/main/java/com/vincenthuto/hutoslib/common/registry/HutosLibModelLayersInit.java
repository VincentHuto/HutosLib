package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HutosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HutosLibModelLayersInit {

	public static final ModelLayerLocation arm_banner = new ModelLayerLocation(
			HutosLib.rloc( "arm_banner"), "main");

	@SubscribeEvent
	public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(arm_banner, ModelArmBanner::createBodyLayer);

	}

}
