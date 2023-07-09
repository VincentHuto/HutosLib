package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BookCodeModel {
	ResourceLocation resourceLocation;
	BookTemplate template;
	List<BookChapterTemplate> chapters;

	public BookCodeModel(ResourceLocation resourceLocation, BookTemplate template, List<BookChapterTemplate> chapters) {
		this.resourceLocation = resourceLocation;
		this.template = template;
		this.chapters = chapters;
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

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public int getTotalPages() {
		int count = 0;
		if (chapters != null) {
			for (BookChapterTemplate chapter : chapters) {
				if (chapter.getPages() != null) {
					for (BookPageTemplate page : chapter.getPages()) {
						count++;
					}
				}
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "Book Title: " + resourceLocation.getPath() + ", Book Name: " + template.getTitle() + " it has "
				+ chapters.size() + " Chapters, and " + getTotalPages() + " pages.";
	}

	public void encodeToBuf(FriendlyByteBuf buf) {
		// Write Book location
		buf.writeResourceLocation(resourceLocation);

		// Write book json
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);
		
//		// Write chapter size
//		buf.writeInt(getChapters().size());
//		
//		// Write chapter jsons
//		for (BookChapterTemplate chapter : getChapters()) {
//			buf.writeUtf(chapter.color);
//			buf.writeUtf(chapter.title);
//			buf.writeUtf(chapter.subtitle);
//			buf.writeUtf(chapter.icon);
//			
//			// Write Page size
//			buf.writeInt(chapter.getPages().size());
//			
//			// Write page jsons
//			for (BookPageTemplate page : chapter.getPages()) {
//				buf.writeInt(page.page);
//				buf.writeUtf(page.title);
//				buf.writeUtf(page.subtitle);
//				buf.writeUtf(page.text);
//				buf.writeUtf(page.icon);
//			}
//		}
	}

}
