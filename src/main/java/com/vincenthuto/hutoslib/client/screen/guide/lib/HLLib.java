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

	public static List<TomeChapter> chapters = new ArrayList<>();
	public static List<GuiGuidePage> introPages = new ArrayList<>();
	public static List<GuiGuidePage> knapperPages = new ArrayList<>();
	public static List<GuiGuidePage> bannerPages = new ArrayList<>();
	public static List<GuiGuidePage> itemsPages = new ArrayList<>();
	public static List<GuiGuidePage> blocksPages = new ArrayList<>();
	public static List<GuiGuidePage> enchantPages = new ArrayList<>();
	public static TomeChapter introChapter, bannerChapter, itemChapter, blockChapter, knapperChapter, enchantChapter;

	@Override
	public List<TomeChapter> getChapters() {
		return chapters;
	}

	@Override
	public GuiGuideTitlePage getTitle() {
		return new HLTitlePage();
	}

	@Override
	public void registerChapters() {
		introChapter = new TomeChapter("Intro", TabColor.WHITE, introPages);
		knapperChapter = new TomeChapter("Knappers", TabColor.BLUE, knapperPages);
		bannerChapter = new TomeChapter("Arm Banners", TabColor.CYAN, bannerPages);
		itemChapter = new TomeChapter("Misc Items", TabColor.YELLOW, itemsPages);
		blockChapter = new TomeChapter("Misc Blocks", TabColor.GREEN, blocksPages);
		enchantChapter = new TomeChapter("Enchantments", TabColor.PURPLE, enchantPages);
		Collections.addAll(chapters, introChapter, knapperChapter, bannerChapter, itemChapter, blockChapter,
				enchantChapter);
	}

	@Override
	public void registerTome() {

		introPages.add(new HLGuideTOC("Intro"));
		introPages.add(new HLGuidePage(1, "Intro", "Introduction", "What even is this?",
				"Hey! hows it going? Huto here hope your days well!"
						+ " Thanks for downloading my library, this probably means"
						+ " your using some other larger mod ive authored that warented explanation in guide form! With that said welcome to HutosLib!"
						+ " While the main purpose of this library mod is to contain shared code my other mods use, I added a few small misc. things in this mod as well."
						+ " Most of these are mainly aesthetic in purpose but a few are also utilitarian like Knappers."
						+ " With all that out of the way, thanks again and I hope you enjoy the mods!"));

		knapperPages.add(new HLGuideTOC("Knappers"));
		knapperPages.add(new HLGuidePage(1, "Knappers", "Easy Shattering!", "Smash all the things!",
				"Ever find yourself struggling to smash blocks with standard tools that are just not right for the job?"
						+ "Are things like Obsidian and other hard yet brittle materials? just a pain to obtain in a modscape that requires loads of it early on?"
						+ " Well look no further, these simple, yet highly specialized tools are made for just that! Simply two sticks and your material of choice in a diagonal and this tool can"
						+ "be yours too!",
				new ItemStack(HLItemInit.iron_knapper.get())));
		knapperPages.add(new HLGuidePage(2, "Knappers", "Tiers", "Upgrade as you go",
				"Like all tools, knappers themselves have tiers and can be upgraded as player progression continues."
						+ " The standard vanilla tiers of wood,stone,iron, diamond, and netherite are all included in this as well as other mods adding their own tiers!",
				new ItemStack(HLItemInit.diamond_knapper.get())));

		bannerPages.add(new HLGuideTOC("Arm Banners"));
		bannerPages.add(new HLGuidePage(1, "Arm Banners", "Arm Banners", "Display your fealty!",
				"Showing off your sweet banner designs using a shield is so last season, "
						+ " not to mention it takes up a precious left hand slot for another weapon or torches!, Now you can slap it on your shoulder with a matching armor type"
						+ " to display to the entire world while leaving your off hand free!",
				new ItemStack(HLItemInit.iron_arm_banner.get())));
		bannerPages.add(new HLGuidePage(2, "Arm Banners", "Patterns", "Custom Designs",
				"Hutoslib and other mods using it also tend to include craftable banner designs as well! "
						+ " These are made with paper and another resource and work at the standard loom for application.",
				new ItemStack(HLItemInit.logo_pattern.get())));

		itemsPages.add(new HLGuideTOC("Misc Items"));
		itemsPages.add(new HLGuidePage(1, "Misc Items", "Flasks", "Control",
				"Flasks serve as a smaller unit of liquid containment, currently unused in their base state, but used by other mods.",
				new ItemStack(HLItemInit.cured_clay_flask.get())));
		itemsPages.add(new HLGuidePage(2, "Misc Items", "Flakes", "Control",
				"Flakes are biproducts of using knappers, currently obsidian are the only kind available "
						+ " and serve as a slight bonus to using the proper tool, 4 flakes can be combinded to reform their parent block.",
				new ItemStack(HLItemInit.obsidian_flakes.get())));

		blocksPages.add(new HLGuideTOC("Misc Blocks"));
		blocksPages.add(new HLGuidePage(1, "Misc Blocks", "Display Glass", "Like a zoo",
				"Display glass is a almost completly invisible block aside from the corners, Used to keep entities in place without applying "
						+ " annoying slowness affects or disabling AI all together.",
				new ItemStack(HLBlockInit.display_glass.get())));
		blocksPages.add(new HLGuidePage(2, "Misc Blocks", "Display Pedestal", "Show off your Gear",
				"Display pedestals are a one item/block storage pedestal that simply shows off in a nice lazy susan style "
						+ " to display around your base. They come in many flavors aside from the standard stone brick to fit whatever base design you may have!",
				new ItemStack(HLBlockInit.display_pedestal.get())));

		enchantPages.add(new HLGuideTOC("Enchantments", "Enchantments"));
		enchantPages.add(new HLGuidePage(1, "Enchantments", "Glimmer", "Shiny!",
				"Glimmer is a completly aesthetic enchantment thats only available by crafting"
						+ " its only use is to apply an enchantment glimmer to items/blocks that normally couldnt obtain them(note blocks given it will lose it when placed)",
				new ItemStack(Items.GLOW_BERRIES)));

		registerChapters();
	}

}
