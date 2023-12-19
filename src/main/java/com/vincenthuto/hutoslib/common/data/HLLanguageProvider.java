package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.HLTextUtils;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class HLLanguageProvider extends LanguageProvider {


	public HLLanguageProvider(PackOutput output, String locale) {
		super(output, HutosLib.MOD_ID, locale);
	}

	@Override
	protected void addTranslations() {
		addBannerTranslation("hutoslib_logo", "HutosLib");
		
		add("item_group.hutoslib.hutoslibtab", "HutosLib:A Library Mod");
		add("container.crafting",  "Crafting");
		add("item.hutoslib.hutoslib_logo.desc", "HutosLibe");

		addArmBannerTranslation("leather");
		addArmBannerTranslation("gold");
		addArmBannerTranslation("iron");
		addArmBannerTranslation("diamond");
		addArmBannerTranslation("obsidian");
		addArmBannerTranslation("netherite");

		addKeyBindTranslations();

		
		for (RegistryObject<Item> i : HLItemInit.ITEMS.getEntries()) {
			addItem(i,
					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
		}
		for (RegistryObject<Item> i : HLItemInit.SPECIALITEMS.getEntries()) {
			addItem(i,
					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
		}
		for (RegistryObject<Item> i : HLItemInit.HANDHELDITEMS.getEntries()) {
			addItem(i,
					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
		}
		for (RegistryObject<Block> b : HLBlockInit.BLOCKS.getEntries()) {
			addBlock(b,
					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.hutoslib.", "")));
		}
		for (RegistryObject<Block> b : HLBlockInit.MODELEDBLOCKS.getEntries()) {
			addBlock(b,
					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.hutoslib.", "")));
		}
	}
	public void addKeyBindTranslations() {
		add("key.toolbanner.category", "HutosLib");
		add("key.banner_slot.slot","Open Arm Banner Slot");
		add("key.armbanner.category", "Open Arm Banner Slot");
	
	}
	
	
	public void addBannerTranslation(String regName, String transName) {
		add("block.minecraft.banner.hutoslib." + regName + ".black", "Black " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".red", "Red " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".green", "Green " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".brown", "Brown " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".blue", "Blue " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".purple", "Purple " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".cyan", "Cyan " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".silver", "Light Gray " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".gray", "Gray " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".pink", "Pink " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".lime", "Lime " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".yellow", "Yellow " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".lightBlue", "Light " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".magenta", "Magenta " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".orange", "Orange " + transName);
		add("block.minecraft.banner.hutoslib." + regName + ".white", "White " + transName);
	}


	public void addArmBannerTranslation(String prefix) {
		add("item.hutoslib." + prefix + "_arm_banner.black",
				"Black " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.red", "Red " + HLTextUtils.convertInitToLang("_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.green",
				"Green " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.brown",
				"Brown " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.blue",
				"Blue " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.purple",
				"Purple " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.cyan",
				"Cyan " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.silver",
				"Light Gray " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.gray",
				"Gray " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.pink",
				"Pink " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.lime",
				"Lime " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.yellow",
				"Yellow " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.lightBlue",
				"Light " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.magenta",
				"Magenta " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.orange",
				"Orange " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
		add("item.hutoslib." + prefix + "_arm_banner.white",
				"White " + HLTextUtils.convertInitToLang(prefix + "_arm_banner"));
	}
}
