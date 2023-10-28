//package com.vincenthuto.hutoslib.client.screen.skillttree;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import org.lwjgl.opengl.GL11;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.vincenthuto.hutoslib.client.HLLocHelper;
//import com.vincenthuto.hutoslib.client.particle.util.ParticleColor;
//import com.vincenthuto.hutoslib.client.screen.HLButtonTextured;
//import com.vincenthuto.hutoslib.client.screen.codex.AbstractMalumScreen;
//import com.vincenthuto.hutoslib.client.screen.codex.BookEntry;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraft.client.resources.sounds.SimpleSoundInstance;
//import net.minecraft.client.sounds.SoundManager;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.sounds.SoundEvents;
//
//public abstract class HLAbstractSkillTreeScreen extends AbstractMalumScreen {
//
//	public float xOffset;
//	public float yOffset;
//	public float cachedXOffset;
//	public float cachedYOffset;
//	public boolean ignoreNextMouseInput;
//
//	public final List<BookEntry> bookObjects = new ArrayList<>();
//
//	public final int bookWidth;
//	public final int bookHeight;
//	public final int bookInsideWidth;
//	public final int bookInsideHeight;
//
//	public final int backgroundImageWidth;
//	public final int backgroundImageHeight;
//
//	protected HLAbstractSkillTreeScreen(int backgroundImageWidth, int backgroundImageHeight) {
//		this(378, 250, 344, 218, backgroundImageWidth, backgroundImageHeight);
//	}
//
//	protected HLAbstractSkillTreeScreen(int bookWidth, int bookHeight, int bookInsideWidth, int bookInsideHeight,
//			int backgroundImageWidth, int backgroundImageHeight) {
//		super(Component.translatable("hutoslib.gui.book.title"));
//		this.bookWidth = bookWidth;
//		this.bookHeight = bookHeight;
//		this.bookInsideWidth = bookInsideWidth;
//		this.bookInsideHeight = bookInsideHeight;
//		this.backgroundImageWidth = backgroundImageWidth;
//		this.backgroundImageHeight = backgroundImageHeight;
//		minecraft = Minecraft.getInstance();
//	}
//
//	public void setupObjects() {
//		bookObjects.clear();
//		this.width = minecraft.getWindow().getGuiScaledWidth();
//		this.height = minecraft.getWindow().getGuiScaledHeight();
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//		int coreX = guiLeft + bookInsideWidth;
//		int coreY = guiTop + bookInsideHeight;
//		int width = 24;
//		int height = 16;
//
//		for (BookEntry entry : getEntries()) {
//			bookObjects.add(new BookEntry(entry.color, entry.text.getString(), coreX + entry.xOffset * width,
//					coreY - entry.yOffset * height, height, entry.getAction()));
//		}
//		faceObject(bookObjects.get(0));
//
//	}
//
//	public abstract Collection<BookEntry> getEntries();
//
//	public void renderBackground(ResourceLocation texture, GuiGraphics graphics, float xModifier, float yModifier) {
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//		int insideLeft = guiLeft + 17;
//		int insideTop = guiTop + 14;
//		float uOffset = (backgroundImageWidth - xOffset) * xModifier;
//		float vOffset = Math.min(backgroundImageHeight - bookInsideHeight,
//				(backgroundImageHeight - bookInsideHeight - yOffset * yModifier));
//		// vOffset-=600;
//
//		if (vOffset <= backgroundImageHeight / 2f) {
//			vOffset = backgroundImageHeight / 2f;
//		}
//		if (uOffset <= 0) {
//			uOffset = 0;
//		}
//		if (uOffset > (bookInsideWidth - 8) / 2f) {
//			uOffset = (bookInsideWidth - 8) / 2f;
//		}
//		RenderSystem.setShader(GameRenderer::getPositionTexShader);
//		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//		RenderSystem.setShaderTexture(0, texture);
//
//		graphics.blit(texture, insideLeft, insideTop, uOffset, vOffset, bookInsideWidth, bookInsideHeight,
//				backgroundImageWidth / 2, backgroundImageHeight / 2);
//		// renderTexture(texture, graphics.pose(), insideLeft, insideTop, uOffset,
//		// vOffset, bookInsideWidth, bookInsideHeight, backgroundImageWidth / 2,
//		// backgroundImageHeight / 2);
//	}
//
//		this.width = minecraft.getWindow().getGuiScaledWidth();
//		this.height = minecraft.getWindow().getGuiScaledHeight();
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//		xOffset = -object.posX + guiLeft + bookInsideWidth;
//		yOffset = -object.posY + guiTop + bookInsideHeight;
//	}
//
//	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
//		xOffset += dragX;
//		yOffset += dragY;
//		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
//	}
//
//	@Override
//	public boolean mouseClicked(double mouseX, double mouseY, int button) {
//		cachedXOffset = xOffset;
//		cachedYOffset = yOffset;
//		return super.mouseClicked(mouseX, mouseY, button);
//	}
//
//	@Override
//	public boolean mouseReleased(double mouseX, double mouseY, int button) {
//		if (ignoreNextMouseInput) {
//			ignoreNextMouseInput = false;
//			return super.mouseReleased(mouseX, mouseY, button);
//		}
//		if (xOffset != cachedXOffset || yOffset != cachedYOffset) {
//			return super.mouseReleased(mouseX, mouseY, button);
//		}
//		for (BookObject object : bookObjects) {
//			if (object.isHovering(this, xOffset, yOffset, mouseX, mouseY)) {
//				object.click(xOffset, yOffset, mouseX, mouseY);
//				break;
//			}
//		}
//		return super.mouseReleased(mouseX, mouseY, button);
//	}
//
//	public boolean isHovering(double mouseX, double mouseY, int posX, int posY, int width, int height) {
//		if (!isInView(mouseX, mouseY)) {
//			return false;
//		} else {
//			return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height;
//		}
//	}
//	public void renderEntries(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
//		for (int i = bookObjects.size() - 1; i >= 0; i--) {
//			BookObject object = bookObjects.get(i);
//			boolean isHovering = object.isHovering(this, xOffset, yOffset, mouseX, mouseY);
//			object.isHovering = isHovering;
//			object.hover = isHovering ? Math.min(object.hover + 1, object.hoverCap()) : Math.max(object.hover - 1, 0);
//			object.render(minecraft, graphics, xOffset, yOffset, mouseX, mouseY, partialTicks);
//		}
//	}
//	public boolean isInView(double mouseX, double mouseY) {
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//		return !(mouseX < guiLeft + 17) && !(mouseY < guiTop + 14) && !(mouseX > guiLeft + (bookWidth - 17))
//				&& !(mouseY > (guiTop + bookHeight - 14));
//	}
//
//	public void cut() {
//		int scale = (int) getMinecraft().getWindow().getGuiScale();
//		int guiLeft = (width - bookWidth) / 2;
//		int guiTop = (height - bookHeight) / 2;
//		int insideLeft = guiLeft + 17;
//		int insideTop = guiTop + 18 + 117;
//		GL11.glScissor(insideLeft * scale, insideTop * scale, bookInsideWidth * scale, (bookInsideHeight + 1) * scale); // do
//	}
//
//	@Override
//	public boolean isPauseScreen() {
//		return false;
//	}
//
//	@Override
//	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//		if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
//			onClose();
//			return true;
//		}
//		return super.keyPressed(keyCode, scanCode, modifiers);
//	}
//
//	public class SkillIcon extends HLButtonTextured {
//
//		ParticleColor color;
//		public boolean isHovering;
//		public float hover;
//
//		public SkillIcon(ParticleColor color, String title, int idIn, int x, int y, int w, int h,
//				Button.OnPress pressedAction) {
//			super(HLLocHelper.guiPrefix("book_tabs.png"), idIn, x, y, w, h, 0, 192, Component.literal(title),
//					pressedAction);
//			this.color = color;
//		}
//
//		public ParticleColor getColor() {
//			return color;
//		}
//
//		public boolean isHovering(HLAbstractSkillTreeScreen screen, float xOffset, float yOffset, double mouseX,
//				double mouseY) {
//			return screen.isHovering(mouseX, mouseY, offsetPosX(xOffset), offsetPosY(yOffset), width, height);
//		}
//
//		public int hoverCap() {
//			return 20;
//		}
//
//		public int offsetPosX(float xOffset) {
//			int guiLeft = (width - HLSkillTree.screen.bookWidth) / 2;
//			return (int) (guiLeft + this.posX + xOffset);
//		}
//
//		public int offsetPosY(float yOffset) {
//			int guiTop = (height - HLSkillTree.screen.bookHeight) / 2;
//			return (int) (guiTop + this.posY + yOffset);
//		}
//
//		public void click(float xOffset, float yOffset, double mouseX, double mouseY) {
//
//		}
//
//		// Plays the clicking noise when the page turn button is pressed
//		@Override
//		public void playDownSound(SoundManager handler) {
//			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1F));
//		}
//	}
//}
