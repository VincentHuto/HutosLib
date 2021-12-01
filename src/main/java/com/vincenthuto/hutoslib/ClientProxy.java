package com.vincenthuto.hutoslib;


import com.vincenthuto.hutoslib.client.screen.guide.lib.HLTitlePage;

import net.minecraft.client.Minecraft;

public class ClientProxy implements IProxy {

	@Override
	public void openGuideGui() {
		Minecraft.getInstance().setScreen(new HLTitlePage());
	}



}
