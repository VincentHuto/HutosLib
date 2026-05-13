package com.vincenthuto.hutoslib.common.data.skilltree;

import net.minecraft.resources.Identifier;

public record TreeDataResource(Identifier Identifier,  TreeDataTemplate template) {

	
	public String[] getSplitPath() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		return split;
	}

	
	public String getTree() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 2) {
			return split[0].replace("/", "");
		}
		return null;
	}

	public String getBranch() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 3) {
			return split[1].replace("/", "");
		}
		return null;
	}

	public String getSkill() {
		String input = Identifier.getPath();
		String[] split = Identifier.getPath().split("/");
		if (split.length == 4) {
			return split[3].replace("/", "");
		}
		return null;
	}

}
