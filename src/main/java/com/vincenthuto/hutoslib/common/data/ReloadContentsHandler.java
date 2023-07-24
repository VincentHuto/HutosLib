package com.vincenthuto.hutoslib.common.data;

import javax.annotation.Nullable;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketSyncBookData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ReloadContentsHandler {
	   public static void dataReloaded(@Nullable ServerPlayer serverPlayer) {
	        // Also reload contents when someone types /reload
	        HutosLib.LOGGER.info("Sending reload packet to clients");
	        	if(serverPlayer != null) {
	    	        HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketSyncBookData());
	        	}else {
	    	        HLPacketHandler.MAINCHANNEL.send(PacketDistributor.ALL.noArg(), new PacketSyncBookData());
	        	}
	        
	    }
}
