package com.vincenthuto.hutoslib.client.screen.guide.lib;

import com.vincenthuto.hutoslib.client.HLLocHelper;
import com.vincenthuto.hutoslib.client.screen.guide.GuiGuideTitlePage;
import com.vincenthuto.hutoslib.client.screen.guide.TomeLib;
import com.vincenthuto.hutoslib.common.item.HLItemInit;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class HLTitlePage extends GuiGuideTitlePage {

	public static HLTitlePage screen;

	public HLTitlePage() {
		super( Component.translatable(""), new ItemStack(HLItemInit.diamond_knapper.get()), HLLib.chapters,
				HLLocHelper.guiPrefix("/guide/guide_overlay.png"));
	}

	@Override
	public TomeLib getOwnerTome() {
		return new HLLib();
	}
	

	public static void openScreenViaItem() {
		openScreen(true);
	}

	public static void openScreen(boolean ignoreNextMouseClick) {
		if (screen == null) {
			screen = new HLTitlePage();
		}
		Minecraft.getInstance().setScreen(screen);
	}
}
