package com.vincenthuto.hutoslib.common.data.book;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class BookCodeModel {
	Identifier resourceLocation;
	BookTemplate template;
	List<ChapterTemplate> chapters;

	public BookCodeModel(Identifier resourceLocation, BookTemplate template) {
		this.resourceLocation = resourceLocation;
		this.template = template;
	}

	public BookTemplate getTemplate() {
		return template;
	}

	public void setTemplate(BookTemplate template) {
		this.template = template;
	}

	public List<ChapterTemplate> getChapters() {
		return chapters;
	}

	public void setChapters(List<ChapterTemplate> chapters) {
		this.chapters = chapters;
	}


	public Identifier getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(Identifier resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public int getTotalPages() {
		int count = 0;
		if (chapters != null) {
			for (ChapterTemplate chapter : chapters) {
				if (chapter.getPages() != null) {
					for (BookDataTemplate page : chapter.getPages()) {
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
		buf.writeIdentifier(resourceLocation);

		// Write book json
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.overlayLoc);
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);

	}

}
