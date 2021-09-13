package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.List;

public class TomeChapter {

	public String category;
	public GuiGuidePageTOC TOC;
	public List<GuiGuidePage> pages;

	public TomeChapter(String category, GuiGuidePageTOC toc, List<GuiGuidePage> pages) {
		this.category = category;
		this.TOC = toc;
		this.pages = pages;
	}

}
