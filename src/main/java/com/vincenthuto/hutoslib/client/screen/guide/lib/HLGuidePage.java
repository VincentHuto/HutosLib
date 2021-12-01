package com.vincenthuto.hutoslib.client.screen.guide.lib;

import com.vincenthuto.hutoslib.client.screen.guide.GuiGuidePage;
import com.vincenthuto.hutoslib.client.screen.guide.TomeLib;

import net.minecraft.world.item.ItemStack;

public class HLGuidePage extends GuiGuidePage {

	public HLGuidePage(int pageNumIn, String catagoryIn) {
		super(pageNumIn, catagoryIn);
	}

	public HLGuidePage(int pageNumIn, String catagoryIn, String textIn) {
		super(pageNumIn, catagoryIn, "", "", ItemStack.EMPTY, textIn);
	}

	public HLGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, String textIn) {
		super(pageNumIn, catagoryIn, titleIn, subtitleIn, ItemStack.EMPTY, textIn);
	}

	public HLGuidePage(int pageNumIn, String catagoryIn, String titleIn, String subtitleIn, String textIn,
			ItemStack icon) {
		super(pageNumIn, catagoryIn, titleIn, subtitleIn, icon, textIn);

	}

	@Override
	public TomeLib getOwnerTome() {
		return new HLLib();
	}

}
