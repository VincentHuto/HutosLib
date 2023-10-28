//package com.vincenthuto.hutoslib.client.screen.skillttree;
//
//import static org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Supplier;
//
//import org.lwjgl.opengl.GL11;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.vincenthuto.hutoslib.HutosLib;
//import com.vincenthuto.hutoslib.client.screen.codex.BookEntry;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.sounds.SoundEvent;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//
//public class HLSkillTree extends HLAbstractSkillTreeScreen {
//    public static final List<BookEntry> ENTRIES = new ArrayList<>();
//	public static HLSkillTree screen;
//	public static final ResourceLocation FRAME_TEXTURE = HutosLib.rloc("textures/gui/skilltree/frame.png");
//	public static final ResourceLocation FADE_TEXTURE = HutosLib.rloc("textures/gui/skilltree/fade.png");
//	public static final ResourceLocation BACKGROUND_TEXTURE = HutosLib.rloc("textures/gui/skilltree/background.png");
//
//	protected HLSkillTree() {
//		super(1024, 2560);
//		minecraft = Minecraft.getInstance();
//		setupEntries();
//		// MinecraftForge.EVENT_BUS.post(new SetupMalumCodexEntriesEvent());
//		setupObjects();
//	}
//
//	private void setupEntries() {
//		ENTRIES.clear();
//		Item EMPTY = ItemStack.EMPTY.getItem();
//
////	        ENTRIES.add(new BookEntry(
////	                "introduction", HLItemInit.gold_arm_banner.get(), 0, 0)
////	                .setObjectSupplier(ImportantEntryObject::new)
////	                .addPage(new TextPage("introduction.2"))
////	                .addPage(new TextPage("introduction.3"))
////	                .addPage(new TextPage("introduction.4"))
////	                .addPage(new TextPage("introduction.5"))
////	        );
//	}
//
//	public static void openScreenViaItem() {
//		openScreen(true);
//	}
//
//	public static void openScreen(boolean ignoreNextMouseClick) {
//		if (screen == null) {
//			screen = new HLSkillTree();
//		}
//		screen = new HLSkillTree();
//		Minecraft.getInstance().setScreen(screen);
//	}
//
//    @Override
//    public Collection<BookEntry> getEntries() {
//        return ENTRIES;
//    }
//    
//	@Override
//	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//
//		renderBackground(BACKGROUND_TEXTURE, pGuiGraphics, 0.1f, 0.4f);
//		GL11.glEnable(GL_SCISSOR_TEST);
//		cut();
//
//		GL11.glDisable(GL_SCISSOR_TEST);
//
//		renderTransparentTexture(pGuiGraphics, guiTop, guiTop);
//
//		pGuiGraphics.blit(FRAME_TEXTURE, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
//		renderEntries(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
//
//	}
//
//	private void renderTransparentTexture(GuiGraphics pGuiGraphics, int guiLeft, int guiTop) {
//		RenderSystem.enableBlend();
//		RenderSystem.enableDepthTest();
//		pGuiGraphics.blit(FADE_TEXTURE, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
//		RenderSystem.disableDepthTest();
//		RenderSystem.disableBlend();
//	}
//
//	@Override
//	public Supplier<SoundEvent> getSweetenerSound() {
//		// TODO Auto-generated method stub
//		return () -> SoundEvents.ALLAY_AMBIENT_WITH_ITEM;
//	}
//
//}
