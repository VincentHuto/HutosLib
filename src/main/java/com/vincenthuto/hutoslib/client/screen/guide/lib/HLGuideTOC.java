package com.vincenthuto.hutoslib.client.screen.guide.lib;

import java.util.List;

import com.vincenthuto.hutoslib.client.screen.guide.GuiGuidePage;
import com.vincenthuto.hutoslib.client.screen.guide.GuiGuidePageTOC;
import com.vincenthuto.hutoslib.client.screen.guide.TomeLib;

public class HLGuideTOC extends GuiGuidePageTOC {

	public HLGuideTOC(String catagoryIn) {
		super(catagoryIn);
	}

	public HLGuideTOC(String catagoryIn, String subtitle) {
		super(catagoryIn, subtitle);
	}

	@Override
	public List<GuiGuidePage> getPages() {
		return getOwnerTome().getMatchingChapters(getCatagory()).pages;

	}

	@Override
	public TomeLib getOwnerTome() {
		return new HLLib();
	}

}
