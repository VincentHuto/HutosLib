package com.vincenthuto.hutoslib.common.data.skilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.List;

public class TreeCodeModel {
	Identifier Identifier;
	TreeTemplate template;
	List<BranchTemplate> branchs;

	public TreeCodeModel(Identifier Identifier, TreeTemplate template) {
		this.Identifier = Identifier;
		this.template = template;
	}

	public TreeTemplate getTemplate() {
		return template;
	}

	public void setTemplate(TreeTemplate template) {
		this.template = template;
	}

	public List<BranchTemplate> getBranchs() {
		return branchs;
	}

	public void setBranchs(List<BranchTemplate> branchs) {
		this.branchs = branchs;
	}


	public Identifier getResourceLocation() {
		return Identifier;
	}

	public void setResourceLocation(Identifier Identifier) {
		this.Identifier = Identifier;
	}

	public int getTotalPages() {
		int count = 0;
		if (branchs != null) {
			for (BranchTemplate branch : branchs) {
				if (branch.getSkills() != null) {
					for (TreeDataTemplate page : branch.getSkills()) {
						count++;
					}
				}
			}
		}

		return count;
	}


	@Override
	public String toString() {
		return "Tree  Title: " + Identifier.getPath() + ", Tree  Name: " + template.getTitle() + " it has "
				+ branchs.size() + " Branchs, and " + getTotalPages() + " pages.";
	}
	public void encodeToBuf(FriendlyByteBuf buf) {
		// Write Tree  location
		buf.writeIdentifier(Identifier);

		// Write book json
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.overlayLoc);
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);

	}

}
