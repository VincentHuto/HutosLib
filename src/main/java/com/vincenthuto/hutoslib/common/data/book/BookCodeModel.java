package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

public class BookCodeModel {
	BookTemplate template;
	List<BookChapterTemplate> chapters;
	List<BookPageTemplate> pages;

	public BookCodeModel(BookTemplate template, List<BookChapterTemplate> chapters, List<BookPageTemplate> pages) {
		this.template = template;
		this.chapters = chapters;
		this.pages = pages;
	}

	public BookTemplate getTemplate() {
		return template;
	}

	public void setTemplate(BookTemplate template) {
		this.template = template;
	}

	public List<BookChapterTemplate> getChapters() {
		return chapters;
	}

	public void setChapters(List<BookChapterTemplate> chapters) {
		this.chapters = chapters;
	}

	public List<BookPageTemplate> getPages() {
		return pages;
	}

	public void setPages(List<BookPageTemplate> pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "Book Name: " + template.getTitle() + " it has " + chapters.size() + " Chapters, and " + pages.size()
				+ " pages.";
	}

}
