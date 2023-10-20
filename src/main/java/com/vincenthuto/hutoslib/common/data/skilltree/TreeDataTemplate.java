package com.vincenthuto.hutoslib.common.data.skilltree;

import com.vincenthuto.hutoslib.common.data.shadow.TypeKeyed.TypeKeyedBase;

import net.minecraft.resources.ResourceLocation;

public abstract class TreeDataTemplate extends TypeKeyedBase<TreeDataTemplate> {
	ResourceLocation location;
	int ordinality;

	// So GSON.toJson doesnt like nonprimatives so imma split this like Im doing the
	// icon item thing
	public TreeDataTemplate(int ordinality) {
		this.ordinality = ordinality;
	}

	public int getOrdinality() {
		return ordinality;
	}

	public void setOrdinality(int ordinality) {
		this.ordinality = ordinality;
	}

	public abstract void setBranch(String branchName);

	public abstract void getSkillScreen(int skillNum, TreeCodeModel tree, BranchTemplate branch);

}
