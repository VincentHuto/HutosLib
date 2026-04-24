package com.vincenthuto.hutoslib.common.data.skilltree;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class TreeCodeModel {
	Identifier resourceLocation;
	TreeTemplate template;
	List<BranchTemplate> branchs;

	public TreeCodeModel(Identifier resourceLocation, TreeTemplate template) {
		this.resourceLocation = resourceLocation;
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
		return resourceLocation;
	}

	public void setResourceLocation(Identifier resourceLocation) {
		this.resourceLocation = resourceLocation;
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
		return "Tree  Title: " + resourceLocation.getPath() + ", Tree  Name: " + template.getTitle() + " it has "
				+ branchs.size() + " Branchs, and " + getTotalPages() + " pages.";
	}
	public void encodeToBuf(FriendlyByteBuf buf) {
		// Write Tree  location
		buf.writeResourceLocation(resourceLocation);

		// Write book json
		buf.writeUtf(template.coverLoc);
		buf.writeUtf(template.overlayLoc);
		buf.writeUtf(template.title);
		buf.writeUtf(template.subtitle);
		buf.writeUtf(template.text);
		buf.writeUtf(template.icon);

	}

}
