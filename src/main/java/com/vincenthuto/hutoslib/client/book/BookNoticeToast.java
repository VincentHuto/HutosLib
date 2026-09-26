package com.vincenthuto.hutoslib.client.book;

import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class BookNoticeToast implements Toast {
    private final BookCodeModel book;
    private final Component title;
    private final int count;
    BookNoticeToast(BookCodeModel book, Component title, int count) {this.book=book;this.title=title;this.count=count;}
    @Override public Visibility render(GuiGraphics graphics, ToastComponent component, long elapsed) {
        var mc=Minecraft.getInstance();
        var theme=BookThemeManager.INSTANCE.resolve(book,null);
        var canvas=new BookCanvas(mc.font,theme);
        ResourceLocation texture=theme.texture("toast");
        if(texture!=null)BookCanvas.blit(graphics,texture,0,0,160,32,0,0,160,32,160,32,0xFFFFFFFF);
        else graphics.blitSprite(ResourceLocation.parse("minecraft:toast/advancement"),0,0,160,32);
        graphics.renderFakeItem(book.getTemplate().getIconItem(),7,8);
        var lines=mc.font.split(title,129);
        for(int i=0;i<Math.min(2,lines.size());i++)graphics.drawString(mc.font,lines.get(i),28,6+i*10,theme.color("ink"),false);
        if(count>1)canvas.text(graphics,Integer.toString(count),"badge","notice",8,24);
        return elapsed>=5000?Visibility.HIDE:Visibility.SHOW;
    }
}
