package com.vincenthuto.hutoslib.client.book;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/** Atlas-painted buttons retain vanilla focus, keyboard activation and narration. */
public final class BookActionButton extends AbstractButton {
    @FunctionalInterface public interface Painter { void draw(GuiGraphics graphics, BookActionButton button, int mouseX, int mouseY); }
    private final Runnable action;
    private final Painter painter;
    private boolean hoveredBefore;
    private long hoverStart;
    private float hoverFrom, hover;

    public BookActionButton(int x, int y, int width, int height, Component label, Runnable action, Painter painter) {
        super(x,y,width,height,label);
        this.action=action;
        this.painter=painter;
    }
    @Override public void onPress() { action.run(); }
    public float hoverProgress(int ticks) {
        boolean target=isHoveredOrFocused();long now=net.minecraft.Util.getMillis();
        if(target!=hoveredBefore) {hoverFrom=hover;hoverStart=now;hoveredBefore=target;}
        float t=ticks==0?1:Math.clamp((now-hoverStart)/(ticks*50f),0,1);
        hover=hoverFrom+((target?1:0)-hoverFrom)*t;
        return hover;
    }
    @Override protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { painter.draw(graphics,this,mouseX,mouseY); }
    @Override protected void updateWidgetNarration(NarrationElementOutput output) { defaultButtonNarrationText(output); }
}
