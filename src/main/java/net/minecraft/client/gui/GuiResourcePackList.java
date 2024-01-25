package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public abstract class GuiResourcePackList extends GuiListExtended {
    protected final Minecraft mc;
    protected final List<ResourcePackListEntry> resourcePackList;

    public GuiResourcePackList(Minecraft mcIn, int width, int height, List<ResourcePackListEntry> resourcePackList) {
        super(mcIn, width, height, 32, height - 55 + 4, 36);
        this.mc = mcIn;
        this.resourcePackList = resourcePackList;
        this.field_148163_i = false;
        this.setHasListHeader((int)(mcIn.fontRendererObj.FONT_HEIGHT * 1.5F));
    }

    protected void drawListHeader(int x, int y, Tessellator tessellator) {
        String header = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + getListHeader();
        this.mc.fontRendererObj.drawString(header, x + this.width / 2 - this.mc.fontRendererObj.getStringWidth(header) / 2, Math.min(this.top + 3, y), 16777215);
    }

    protected abstract String getListHeader();

    public List<ResourcePackListEntry> getList() {
        return this.resourcePackList;
    }

    protected int getSize() {
        return this.getList().size();
    }

    public ResourcePackListEntry getListEntry(int index) {
        return this.getList().get(index);
    }

    public int getListWidth() {
        return this.width;
    }

    protected int getScrollBarX() {
        return this.right - 6;
    }
}