package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.List;

public abstract class TomeLib {

	public abstract List<TomeChapter> getChapters();

	public TomeChapter getMatchingChapters(String category) {
		for (TomeChapter element : getChapters()) {
			if (element.category == category) {
				return element;
			}
		}
		return null;
	}

	public abstract GuiGuideTitlePage getTitle();

	public abstract void registerChapters();

	public abstract void registerTome();
}
