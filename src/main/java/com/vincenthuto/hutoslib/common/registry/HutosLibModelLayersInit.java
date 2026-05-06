package com.vincenthuto.hutoslib.common.registry;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.model.item.ModelArmBanner;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = HutosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HutosLibModelLayersInit {

	public static final ModelLayerLocation arm_banner = new ModelLayerLocation(
			HutosLib.rloc( "arm_banner"), "main");

	@SubscribeEvent
	public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(arm_banner, ModelArmBanner::createBodyLayer);

	}

}
