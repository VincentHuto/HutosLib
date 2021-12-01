package com.vincenthuto.hutoslib.client.screen.guide;

import java.util.List;

public abstract class TomeLib {

	public abstract void registerChapters();

	public abstract void registerTome();

	public abstract GuiGuideTitlePage getTitle();

	public abstract List<TomeChapter> getChapters();

	public TomeChapter getMatchingChapters(String category) {
		for (int i = 0; i < getChapters().size(); i++) {
			if (getChapters().get(i).category == category) {
				return getChapters().get(i);
			}
		}
		return null;
	}
}
