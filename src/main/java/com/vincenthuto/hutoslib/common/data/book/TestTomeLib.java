package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

public abstract class TestTomeLib {

	BookCodeModel book;

	public TestTomeLib(BookCodeModel bookIn) {
		this.book = bookIn;
	}

	public abstract List<TestTomeChapter> getChapters();

	public abstract TestGuiGuideTitlePage getTitle();

	public abstract void registerTome();

	public TestTomeChapter getMatchingChapters(String category) {
		for (TestTomeChapter element : getChapters()) {
			if (element.category == category) {
				return element;
			}
		}
		return null;
	}

}
