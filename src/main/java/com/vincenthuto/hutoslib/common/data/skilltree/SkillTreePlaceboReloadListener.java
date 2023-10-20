package com.vincenthuto.hutoslib.common.data.skilltree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.shadow.PlaceboJsonReloadListener;

import net.minecraft.resources.ResourceLocation;

public class SkillTreePlaceboReloadListener extends PlaceboJsonReloadListener<TreeDataTemplate> {

	public static final SkillTreePlaceboReloadListener INSTANCE = new SkillTreePlaceboReloadListener();
	private Map<ResourceLocation, TreeDataTemplate> byType = ImmutableMap.of();
	public List<TreeCodeModel> trees = ImmutableList.of();
	private static final Gson GSON = new Gson();

	public SkillTreePlaceboReloadListener() {
		super(HutosLib.LOGGER, "skilltrees", true, true);
	}
	
	public TreeCodeModel getTreeByTitle(ResourceLocation rl) {
		Optional<TreeCodeModel> optional = this.trees.parallelStream().filter(p -> p.getResourceLocation().equals(rl))
				.findFirst();
		return optional.isPresent() ? optional.get() : null;
	}

	@Override
	protected void onReload() {
		super.onReload();
		ImmutableMap.Builder<ResourceLocation, TreeDataTemplate> builder = ImmutableMap.builder();
		this.registry.values().forEach(a -> {
			builder.put(a.id, a);
		});
		this.byType = builder.build();
		this.bindTrees(this.byType);
	}

	@Override
	protected void registerBuiltinSerializers() {
		this.registerSerializer(HutosLib.rloc("tree"), TreeTemplate.SERIALIZER);
		this.registerSerializer(HutosLib.rloc("branch"), BranchTemplate.SERIALIZER);
		this.registerSerializer(HutosLib.rloc("skill"), SkillTemplate.SERIALIZER);
	}

	public Map<ResourceLocation, TreeDataTemplate> getTypeMap() {
		return this.byType;
	}

	public List<TreeCodeModel> getTrees() {
		return this.trees;
	}

	protected String resourceName() {
		return "trees";
	}

	public void bindTrees(Map<ResourceLocation, TreeDataTemplate> resourceManager) {
		HutosLib.LOGGER.info("Binding Trees:");
		ImmutableList.Builder<TreeCodeModel> builder = ImmutableList.builder();

		// Sort resources
		HutosLib.LOGGER.info("Sorting resources");
		List<TreeDataResource> resources = new ArrayList<TreeDataResource>();
		resourceManager.forEach((rLoc, template) -> {
			resources.add(new TreeDataResource(rLoc, template));
		});

		List<TreeDataResource> treeNames = new ArrayList<TreeDataResource>();
		List<TreeDataResource> branchNames = new ArrayList<TreeDataResource>();
		List<TreeDataResource> skillNames = new ArrayList<TreeDataResource>();

		for (TreeDataResource resource : resources) {
			if (resource.getTree() != null) {
				treeNames.add(resource);
			} else if (resource.getBranch() != null) {
				branchNames.add(resource);
			} else if (resource.getSkill() != null) {
				skillNames.add(resource);
			}
		}

		for (int i = 0; i < treeNames.size(); i++) {
			if (treeNames.get(i).template() instanceof TreeTemplate b) {
				String treeTitle = treeNames.get(i).getTree();
				TreeCodeModel tree = new TreeCodeModel(new ResourceLocation(
						treeNames.get(i).resourceLocation().getNamespace(), treeNames.get(i).getTree()), b);
				List<BranchTemplate> branchs = new ArrayList<BranchTemplate>();
				for (int j = 0; j < branchNames.size(); j++) {
					String branchTitle = branchNames.get(j).getSplitPath()[1];
					String branchTree = branchNames.get(j).getSplitPath()[0];
					if (branchNames.get(j).template() instanceof BranchTemplate c) {
						if (branchTree.equals(treeTitle)) {
							List<TreeDataTemplate> skills = new ArrayList<TreeDataTemplate>();
							for (int k = 0; k < skillNames.size(); k++) {
								String skillTree = skillNames.get(k).getSplitPath()[0];
								String skillBranch = skillNames.get(k).getSplitPath()[1];
								if (skillTree.equals(treeTitle) && skillBranch.equals(branchTitle)) {
									skills.add(skillNames.get(k).template());
								}
							}
							Collections.sort(skills,
									(obj1, obj2) -> Integer.compare(obj1.getOrdinality(), obj2.getOrdinality()));
							c.setSkills(skills);
							branchs.add(c);

						}
					}
				}
				tree.setBranchs(branchs);
				builder.add(tree);

			}
		}
		this.trees = builder.build();

		HutosLib.LOGGER.info(trees.size() + " Trees Formed!");

	}

}
