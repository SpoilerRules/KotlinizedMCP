package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.guimainmenu.GuiMainMenu;
import net.minecraft.client.resources.LocalizationHelper;

import java.io.IOException;

public class GuiPauseScreen extends GuiScreen {

    public void initGui() {
        buttonList.clear();
        int i = -16;
        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + i, LocalizationHelper.translate("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning()) {
            buttonList.get(0).displayString = LocalizationHelper.translate("menu.disconnect");
        }

        buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + i, LocalizationHelper.translate("menu.returnToGame")));
        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + i, 98, 20, LocalizationHelper.translate("menu.options")));
        GuiButton guibutton;
        buttonList.add(guibutton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, LocalizationHelper.translate("menu.shareToLan")));
        buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, LocalizationHelper.translate("gui.achievements")));
        buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, LocalizationHelper.translate("gui.stats")));
        guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                boolean flag = this.mc.isIntegratedServerRunning();
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);

                if (flag) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                } else {
                    this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                }

            case 2:
            case 3:
            default:
                break;

            case 4:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;

            case 7:
                this.mc.displayGuiScreen(new GuiShareToLAN(this));
        }
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("menu.game"), this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
