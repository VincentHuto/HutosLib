package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import com.vincenthuto.hutoslib.client.screen.guide.TomeCategoryTab.TabColor;

public class TestTomeChapter {

	public String category;
	public TestGuiGuidePageTOC TOC;
	public List<TestGuiGuidePage> pages;
	public TabColor color;

	public TestTomeChapter(String category, TabColor color, TestGuiGuidePageTOC toc, List<TestGuiGuidePage> pages) {
		this.category = category;
		this.color = color;
		this.TOC = toc;
		this.pages = pages;
	}
}
