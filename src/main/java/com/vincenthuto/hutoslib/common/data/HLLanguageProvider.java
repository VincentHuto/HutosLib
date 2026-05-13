package com.vincenthuto.hutoslib.common.data;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.util.HLTextUtils;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.common.data.LanguageProvider;

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
		add("tooltip.hutoslib.arm_banner.base_color", "Base Color: %s");

		addArmBannerTranslation("leather");
		addArmBannerTranslation("gold");
		addArmBannerTranslation("iron");
		addArmBannerTranslation("diamond");
		addArmBannerTranslation("obsidian");
		addArmBannerTranslation("netherite");

		addKeyBindTranslations();

		
		for (var i : HLItemInit.ITEMS.getEntries()) {
			if (!(i.get() instanceof BlockItem)) {
				add(i.get(),
						HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
			}
		}
		for (var i : HLItemInit.SPECIALITEMS.getEntries()) {
			add(i.get(),
					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
		}
		for (var i : HLItemInit.HANDHELDITEMS.getEntries()) {
			add(i.get(),
					HLTextUtils.convertInitToLang(i.get().asItem().getDescriptionId().replace("item.hutoslib.", "")));
		}
		for (var b : HLBlockInit.BLOCKS.getEntries()) {
			add(b.get(),
					HLTextUtils.convertInitToLang(b.get().asItem().getDescriptionId().replace("block.hutoslib.", "")));
		}
		for (var b : HLBlockInit.MODELEDBLOCKS.getEntries()) {
			add(b.get(),
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
		String itemName = HLTextUtils.convertInitToLang(prefix + "_arm_banner");
		for (DyeColor color : DyeColor.values()) {
			add("item.hutoslib." + prefix + "_arm_banner." + color.getName(),
					HLTextUtils.convertInitToLang(color.getName()) + " " + itemName);
		}
	}
}
