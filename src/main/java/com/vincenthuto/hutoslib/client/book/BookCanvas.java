package com.vincenthuto.hutoslib.client.book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vincenthuto.hutoslib.common.book.BookText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

/** Small native-pixel drawing primitives shared by the reader views. */
public final class BookCanvas {
    private final Font font;
    private final BookVisualTheme theme;
    public BookCanvas(Font font, BookVisualTheme theme) { this.font = font; this.theme = theme; }

    public Component text(String value, String role) {
        ResourceLocation face = theme.font(role);
        return Component.literal(value).withStyle(face == null ? Style.EMPTY : Style.EMPTY.withFont(face));
    }

    public Component run(BookText.Run run, String role) {
        return text(run.text().replace("\n", ""), role).copy().withStyle(style -> style.withBold(run.bold()).withItalic(run.italic()));
    }

    public int width(String value, String role) { return font.width(text(value, role)); }
    public int width(BookText.Run run, String role) { return font.width(run(run, role)); }

    public String headingFace() { return theme.font("title20") != null ? "title20" : "title"; }
    public int headingLineHeight() { return theme.font("title20") != null ? 20 : 16; }

    public void text(GuiGraphics graphics, String value, String face, String color, int x, int y) {
        boolean title = face.equals("title") || face.equals("title20") || face.equals("title24") || face.equals("title28");
        graphics.drawString(font, text(value, face), x, y, theme.color(color), title && theme.font("title20") == null);
    }

    public void centered(GuiGraphics graphics, String value, String face, String color, int center, int y) {
        text(graphics, value, face, color, center - width(value, face) / 2, y);
    }

    public void fittedText(GuiGraphics graphics, String value, String face, String color, int x, int y, int available) {
        float scale=Math.min(1f,(float)available/Math.max(1,width(value,face)));
        graphics.pose().pushPose();
        graphics.pose().translate(x,y,0);
        graphics.pose().scale(scale,scale,1);
        text(graphics,value,face,color,0,0);
        graphics.pose().popPose();
    }

    public String ellipsis(String value, String face, int available) {
        if (width(value, face) <= available) return value;
        int end = value.length();
        while (end > 0 && width(value.substring(0, end) + "…", face) > available) end = value.offsetByCodePoints(end, -1);
        return end == 0 && width("…", face) > available ? "" : value.substring(0, end) + "…";
    }

    public List<String> heading(String value, String face, int available) {
        if (width(value, face) <= available) return List.of(value);
        int split = 0, lastSpace = -1;
        while (split < value.length()) {
            int next = value.offsetByCodePoints(split, 1);
            if (width(value.substring(0, next), face) > available) break;
            if (Character.isWhitespace(value.codePointAt(split))) lastSpace = split;
            split = next;
        }
        if (lastSpace > 0) split = lastSpace;
        if (split == 0) return List.of(ellipsis(value, face, available));
        return List.of(value.substring(0, split).stripTrailing(), ellipsis(value.substring(split).stripLeading(), face, available));
    }

    public void sprite(GuiGraphics graphics, BookAtlas region, int x, int y, int u, int v, int width, int height, int tint) {
        var sprite = theme.sprite(region);
        blit(graphics, sprite.texture(), x, y, width, height, sprite.u() + u, sprite.v() + v,
                width, height, sprite.textureWidth(), sprite.textureHeight(), tint);
    }

    public void panel(GuiGraphics graphics, BookAtlas region, int x, int y, int width, int height, int inset, int tint) {
        var sprite = theme.sprite(region);
        sliced(graphics, sprite.texture(), x, y, width, height, sprite.u(), sprite.v(), region.width, region.height,
                sprite.textureWidth(), sprite.textureHeight(), inset, tint);
    }

    public void shell(GuiGraphics graphics, BookGeometry geometry, boolean cover) {
        int x = geometry.left(), y = geometry.top(), w = geometry.outerWidth(), h = geometry.outerHeight();
        if (geometry.compact()) { w = geometry.viewportWidth(); h = geometry.viewportHeight(); }
        graphics.fill(x + 4, y + 4, x + w + 4, y + h + 4, 0x73000000);
        for (int i = 3; i >= 1; i--) {
            int color = theme.color(i == 3 ? "frameOuter" : i == 2 ? "frameMid" : "frameInner");
            graphics.fill(x-i,y-i,x+w+i,y-i+1,color);
            graphics.fill(x-i,y+h+i-1,x+w+i,y+h+i,color);
            graphics.fill(x-i,y-i,x-i+1,y+h+i,color);
            graphics.fill(x+w+i-1,y-i,x+w+i,y+h+i,color);
        }
        if (theme.legacy() && !geometry.spread()) {
            ResourceLocation texture = theme.texture(cover ? "cover" : "page");
            if (texture != null) graphics.blit(texture,x,y,0,0,w,h);
            else graphics.fill(x,y,x+w,y+h,theme.color("paper"));
            if (cover && theme.texture("overlay") != null) graphics.blit(theme.texture("overlay"),x,y,0,0,w,h);
            return;
        }
        if (geometry.spread()) {
            graphics.fill(x,y,x+w,y+h,theme.color("board"));
            leaf(graphics,x+4,y+4,170,geometry.leafHeight());
            leaf(graphics,x+174,y+4,170,geometry.leafHeight());
            ResourceLocation spread = theme.texture("spread");
            if (spread != null) roundedCrease(graphics,spread,x+162,y,h);
            else sprite(graphics,BookAtlas.Sp,x+162,y+4,0,0,24,24,0xFFFFFFFF);
        } else if (cover && theme.texture("cover") != null && !geometry.compact()) {
            blit(graphics,theme.texture("cover"),x,y,w,h,0,0,174,228,174,228,0xFFFFFFFF);
        } else leaf(graphics,x,y,w,h);
    }

    private static void roundedCrease(GuiGraphics graphics, ResourceLocation texture, int x, int y, int height) {
        // The paper starts four pixels into the texture. Reveal the page border beneath
        // each rounded corner instead of covering it with the rectangular spine strip.
        int[] insets = {5, 3, 2, 1, 1, 0};
        for (int row = 0; row < insets.length; row++) {
            int inset = insets[row], width = 24 - 2 * inset;
            int top = 4 + row, bottom = height - 5 - row;
            blit(graphics,texture,x+inset,y+top,width,1,inset,top,width,1,24,232,0xFFFFFFFF);
            blit(graphics,texture,x+inset,y+bottom,width,1,inset,227-row,width,1,24,232,0xFFFFFFFF);
        }
        blit(graphics,texture,x,y+10,24,height-20,0,10,24,212,24,232,0xFFFFFFFF);
    }

    private void leaf(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x,y,x+width,y+height,theme.color("paper"));
        ResourceLocation paper = theme.texture("paper");
        if (paper != null) {
            for (int row=0; row<height; row+=64) for (int column=0; column<width; column+=64) {
                int w=Math.min(64,width-column), h=Math.min(64,height-row);
                blit(graphics,paper,x+column,y+row,w,h,0,0,w,h,64,64,theme.color("paper"));
            }
        }
        ResourceLocation page = theme.texture("page");
        if (page != null) sliced(graphics,page,x,y,width,height,0,0,48,48,48,48,8,0xFFFFFFFF);
    }

    public void wash(GuiGraphics graphics, int x, int y, int width, int height, float alpha) {
        if (width <= 0 || alpha <= 0) return;
        int tint = theme.color("wash") & 0xFFFFFF | Math.clamp(Math.round(alpha * 255), 0, 255) << 24;
        panel(graphics,BookAtlas.W,x,y,width,height,2,tint);
    }

    public void ribbon(GuiGraphics graphics,int x,int y,int height,int tint) {
        sprite(graphics,BookAtlas.Rb,x,y,0,0,7,3,tint);
        for(int offset=3;offset<height-6;offset++) sprite(graphics,BookAtlas.Rb,x,y+offset,0,3,7,1,tint);
        sprite(graphics,BookAtlas.Rb,x,y+height-6,0,42,7,6,tint);
    }

    public void decal(GuiGraphics graphics,com.vincenthuto.hutoslib.common.data.book.PageTemplate page,int x,int y) {
        var authored=page.getPresentation().decal();
        if(authored.isPresent()&&authored.get().left().isPresent())return;
        List<ResourceLocation> choices=theme.decals();
        if(choices.isEmpty())return;
        ResourceLocation texture;
        if(authored.isPresent()) {
            String name=authored.get().right().orElse("");
            texture=choices.stream().filter(id->id.toString().equals(name)||id.getPath().endsWith("/"+name+".png")).findFirst().orElse(null);
        } else {
            long sample=Integer.toUnsignedLong((page.getId()+"/decal-chance").hashCode());
            if(sample/(double)(1L<<32)>=theme.layout().decalChance())return;
            texture=choices.get(Math.floorMod(page.getId().toString().hashCode(),choices.size()));
        }
        if(texture!=null) {
            var size=BookThemeManager.INSTANCE.dimensions(texture);
            blit(graphics,texture,x,y,size[0],size[1],0,0,size[0],size[1],size[0],size[1],0xFFFFFFFF);
        }
    }

    public static String roman(int value) {
        if (value <= 0) return "—";
        StringBuilder out = new StringBuilder();
        int[] numbers = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] letters = {"m","cm","d","cd","c","xc","l","xl","x","ix","v","iv","i"};
        for (int i=0; i<numbers.length; i++) while (value >= numbers[i]) { value-=numbers[i]; out.append(letters[i]); }
        return out.toString();
    }

    private static void sliced(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                               int u, int v, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, int inset, int tint) {
        int edgeX=Math.min(inset,width/2), edgeY=Math.min(inset,height/2);
        int[] xs={0,edgeX,width-edgeX,width}, ys={0,edgeY,height-edgeY,height};
        int[] us={u,u+inset,u+sourceWidth-inset,u+sourceWidth}, vs={v,v+inset,v+sourceHeight-inset,v+sourceHeight};
        for (int row=0; row<3; row++) for (int col=0; col<3; col++) {
            if (xs[col+1] > xs[col] && ys[row+1] > ys[row])
                blit(graphics,texture,x+xs[col],y+ys[row],xs[col+1]-xs[col],ys[row+1]-ys[row],us[col],vs[row],
                        us[col+1]-us[col],vs[row+1]-vs[row],textureWidth,textureHeight,tint);
        }
    }

    public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                            int u, int v, int sourceWidth, int sourceHeight, int textureWidth, int textureHeight, int tint) {
        RenderSystem.setShaderColor((tint>>16&255)/255f,(tint>>8&255)/255f,(tint&255)/255f,(tint>>>24)/255f);
        graphics.blit(texture,x,y,width,height,(float)u,(float)v,sourceWidth,sourceHeight,textureWidth,textureHeight);
        RenderSystem.setShaderColor(1,1,1,1);
    }
}
