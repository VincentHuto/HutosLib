package com.vincenthuto.hutoslib.client.screen.skillttree;

import static org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.client.screen.guide.HLGuiGuideTitlePage;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
public class HLSkillTree extends HLAbstractSkillTreeScreen {
	public static HLSkillTree screen;
	public static final ResourceLocation FRAME_TEXTURE = HutosLib.rloc("textures/gui/skilltree/frame.png");
	public static final ResourceLocation FADE_TEXTURE = HutosLib.rloc("textures/gui/skilltree/fade.png");
	public static final ResourceLocation BACKGROUND_TEXTURE = HutosLib.rloc("textures/gui/skilltree/background.png");

	protected HLSkillTree() {
		super(1024, 2560);
		minecraft = Minecraft.getInstance();
//        setupEntries();
//        MinecraftForge.EVENT_BUS.post(new SetupMalumCodexEntriesEvent());
//        setupObjects();
	}
	

	public static void openScreenViaItem() {
		openScreen(true);
	}

	public static void openScreen( boolean ignoreNextMouseClick) {
		if (screen == null) {
			screen = new HLSkillTree();
		}
		screen = new HLSkillTree();
		Minecraft.getInstance().setScreen(screen);
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		int guiLeft = (width - bookWidth) / 2;
		int guiTop = (height - bookHeight) / 2;

		renderBackground(BACKGROUND_TEXTURE, pGuiGraphics,0.1f, 0.4f);
		GL11.glEnable(GL_SCISSOR_TEST);
		cut();

//		renderEntries(poseStack, mouseX, mouseY, partialTicks);
//		ScreenParticleHandler.renderEarlyParticles();
		GL11.glDisable(GL_SCISSOR_TEST);
		
		
	   // renderTransparentTexture(FADE_TEXTURE, poseStack, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
	    pGuiGraphics.blit(FRAME_TEXTURE,  guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
       // lateEntryRender(poseStack, mouseX, mouseY, partialTicks);

	}

}
