package com.vincenthuto.hutoslib.common.data.book;

import net.minecraft.resources.Identifier;

public record BookDataResource(Identifier Identifier,  BookDataTemplate template) {

	
	public String[] getSplitPath() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		return split;
	}

	
	public String getBook() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 2) {
			return split[0].replace("/", "");
		}
		return null;
	}

	public String getChapter() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 3) {
			return split[1].replace("/", "");
		}
		return null;
	}

	public String getPage() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 4) {
			return split[3].replace("/", "");
		}
		return null;
	}

}
