package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.List;

import com.vincenthuto.hutoslib.client.screen.guide.TomeCategoryTab.TabColor;
import com.vincenthuto.hutoslib.client.screen.guide.lib.HLGuideTOC;

public class TomeChapter {

	public String category;
	public GuiGuidePageTOC TOC;
	public List<GuiGuidePage> pages;
	public TabColor color;

	public TomeChapter(String category, TabColor color, GuiGuidePageTOC toc, List<GuiGuidePage> pages) {
		this.category = category;
		this.color = color;
		this.TOC = toc;
		this.pages = pages;
	}

}
