package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiClickableScrolledSelectionListProxy extends GuiSlot
{

    public GuiClickableScrolledSelectionListProxy(int p_i45526_2_, int p_i45526_3_, int p_i45526_4_, int p_i45526_5_, int p_i45526_6_)
    {
        super(Minecraft.getMinecraft(), p_i45526_2_, p_i45526_3_, p_i45526_4_, p_i45526_5_, p_i45526_6_);
    }

    public int func_178044_e()
    {
        return super.width;
    }

    public int func_178042_f()
    {
        return super.mouseY;
    }

    public int func_178045_g()
    {
        return super.mouseX;
    }
    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn)
    {
        int i = this.getSize();

        for (int j = 0; j < i; ++j)
        {
            int k = p_148120_2_ + j * this.slotHeight + this.headerPadding;
            int l = this.slotHeight - 4;

            if (k > this.bottom || k + l < this.top)
            {
                this.setSelected(j, p_148120_1_, k);
            }

            this.drawSlot(j, p_148120_1_, k, l, mouseXIn, mouseYIn);
        }
    }
}
