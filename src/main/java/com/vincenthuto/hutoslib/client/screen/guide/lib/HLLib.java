package com.vincenthuto.hutoslib.client.screen.guide.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vincenthuto.hutoslib.client.screen.guide.GuiGuidePage;
import com.vincenthuto.hutoslib.client.screen.guide.GuiGuideTitlePage;
import com.vincenthuto.hutoslib.client.screen.guide.TomeCategoryTab.TabColor;
import com.vincenthuto.hutoslib.client.screen.guide.TomeChapter;
import com.vincenthuto.hutoslib.client.screen.guide.TomeLib;
import com.vincenthuto.hutoslib.common.block.HLBlockInit;
import com.vincenthuto.hutoslib.common.item.HLItemInit;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HLLib extends TomeLib {

	public static List<TomeChapter> chapters = new ArrayList<TomeChapter>();;
	public static List<GuiGuidePage> introPages = new ArrayList<GuiGuidePage>();
	public static List<GuiGuidePage> knapperPages = new ArrayList<GuiGuidePage>();
	public static List<GuiGuidePage> bannerPages = new ArrayList<GuiGuidePage>();
	public static List<GuiGuidePage> itemsPages = new ArrayList<GuiGuidePage>();
	public static List<GuiGuidePage> blocksPages = new ArrayList<GuiGuidePage>();
	public static List<GuiGuidePage> enchantPages = new ArrayList<GuiGuidePage>();
	public static TomeChapter introChapter, bannerChapter, itemChapter, blockChapter, knapperChapter, enchantChapter;

	@Override
	public void registerTome() {

		introPages.add(new HLGuideTOC("Intro"));
		introPages.add(new HLGuidePage(1, "Intro", "MANUIUIDI*WQJHDUIWQHNUIJDWQHBNUDJIHNUIQ"));
		introPages.add(new HLGuidePage(2, "Intro", "Intro", "Getting to know yourself",
				"ejfiuoejhwiofehjwiofhnjewifnhjuew", new ItemStack(Items.BOOK)));

		knapperPages.add(new HLGuideTOC("Knappers"));
		knapperPages.add(new HLGuidePage(1, "Knappers", "Iron", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.iron_knapper.get())));
		knapperPages.add(new HLGuidePage(2, "Knappers", "Diamond", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.diamond_knapper.get())));

		bannerPages.add(new HLGuideTOC("ArmBanners"));
		bannerPages.add(new HLGuidePage(1, "ArmBanners", "Arm Banners", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.iron_arm_banner.get())));
		bannerPages.add(new HLGuidePage(2, "ArmBanners", "Quirks of them", "Control",
				"ejfiuoejhwiofehjwiofhnjewifnhjuew", new ItemStack(HLItemInit.netherite_arm_banner.get())));

		itemsPages.add(new HLGuideTOC("Misc Items"));
		itemsPages.add(new HLGuidePage(1, "Misc Items", "Flasks", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.cured_clay_flask.get())));
		itemsPages.add(new HLGuidePage(2, "Misc Items", "Flakes", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.obsidian_flakes.get())));
		itemsPages.add(new HLGuidePage(3, "Misc Items", "Patterns", "Control", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(HLItemInit.logo_pattern.get())));

		blocksPages.add(new HLGuideTOC("Misc Blocks"));
		blocksPages.add(new HLGuidePage(1, "Misc Blocks", "Display Glass", "Control",
				"ejfiuoejhwiofehjwiofhnjewifnhjuew", new ItemStack(HLBlockInit.display_glass.get())));
		blocksPages.add(new HLGuidePage(2, "Misc Blocks", "Display Pedestal", "Control",
				"ejfiuoejhwiofehjwiofhnjewifnhjuew", new ItemStack(HLBlockInit.display_pedestal.get())));

		enchantPages.add(new HLGuideTOC("Enchantments","Enchantments"));
		enchantPages.add(new HLGuidePage(1, "Enchantments", "Glimmer", "Shiny!", "ejfiuoejhwiofehjwiofhnjewifnhjuew",
				new ItemStack(Items.GLOW_BERRIES)));

		registerChapters();
	}

	@Override
	public void registerChapters() {
		introChapter = new TomeChapter("Intro", TabColor.WHITE, introPages);
		knapperChapter = new TomeChapter("Knappers", TabColor.BLUE, knapperPages);
		bannerChapter = new TomeChapter("ArmBanners", TabColor.CYAN, bannerPages);
		itemChapter = new TomeChapter("Misc Items", TabColor.YELLOW, itemsPages);
		blockChapter = new TomeChapter("Misc Blocks", TabColor.GREEN, blocksPages);
		enchantChapter = new TomeChapter("Enchantments", TabColor.PURPLE, enchantPages);
		Collections.addAll(chapters, introChapter, knapperChapter, bannerChapter, itemChapter, blockChapter,
				enchantChapter);
	}

	@Override
	public GuiGuideTitlePage getTitle() {
		return new HLTitlePage();
	}

	@Override
	public List<TomeChapter> getChapters() {
		return chapters;
	}

}
