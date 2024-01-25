package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
    public GuiListExtended(Minecraft minecraft, int width, int height, int top, int bottom, int slotHeight) {
        super(minecraft, width, height, top, bottom, slotHeight);
    }

    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
    }

    protected boolean isElementSelected(int slotIndex) {
        return false;
    }

    protected void drawBackground() {
    }

    protected void drawSlot(int entryID, int x, int y, int slotHeight, int mouseX, int mouseY) {
        getListEntry(entryID).drawEntry(entryID, x, y, getListWidth(), slotHeight, mouseX, mouseY, getSlotIndexFromScreenCoords(mouseX, mouseY) == entryID);
    }

    protected void setSelected(int index, int x, int y) {
        getListEntry(index).setSelected(index, x, y);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        if (isMouseYWithinSlotBounds(mouseY)) {
            int index = getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (index >= 0) {
                int slotX = left + width / 2 - getListWidth() / 2 + 2;
                int slotY = top + 4 - getAmountScrolled() + index * slotHeight + headerPadding;
                int relativeX = mouseX - slotX;
                int relativeY = mouseY - slotY;

                if (getListEntry(index).mousePressed(index, mouseX, mouseY, mouseEvent, relativeX, relativeY)) {
                    setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int mouseEvent) {
        for (int i = 0; i < getSize(); ++i) {
            int slotX = left + width / 2 - getListWidth() / 2 + 2;
            int slotY = top + 4 - getAmountScrolled() + i * slotHeight + headerPadding;
            int relativeX = mouseX - slotX;
            int relativeY = mouseY - slotY;
            getListEntry(i).mouseReleased(i, mouseX, mouseY, mouseEvent, relativeX, relativeY);
        }

        setEnabled(true);
        return false;
    }

    public abstract IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry {
        void setSelected(int index, int x, int y);

        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY);

        void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}