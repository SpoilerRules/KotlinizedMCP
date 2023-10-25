package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.LocalizationHelper;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class GuiShareToLAN extends GuiScreen {
    private final GuiScreen parentScreen;
    private GuiButton startButton;
    private GuiButton cancelButton;
    private String selectedGameMode = "survival";
    private boolean allowCommands;

    public GuiShareToLAN(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, LocalizationHelper.translate("lanServer.start")));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, LocalizationHelper.translate("gui.cancel")));

        this.buttonList.add(this.cancelButton = new GuiButton(104, this.width / 2 - 155, 100, 150, 20, LocalizationHelper.translate("selectWorld.gameMode")));
        this.buttonList.add(this.startButton = new GuiButton(103, this.width / 2 + 5, 100, 150, 20, LocalizationHelper.translate("selectWorld.allowCommands")));
        this.updateButtonText();
    }

    private void updateButtonText() {
        this.cancelButton.displayString = LocalizationHelper.translate("selectWorld.gameMode") + " " + LocalizationHelper.translate("selectWorld.gameMode." + this.selectedGameMode);
        this.startButton.displayString = LocalizationHelper.translate("selectWorld.allowCommands") + " ";

        if (this.allowCommands) {
            this.startButton.displayString = this.startButton.displayString + LocalizationHelper.translate("options.on");
        } else {
            this.startButton.displayString = this.startButton.displayString + LocalizationHelper.translate("options.off");
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 102) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 104) {
            switch (this.selectedGameMode) {
                case "spectator" -> this.selectedGameMode = "creative";
                case "creative" -> this.selectedGameMode = "adventure";
                case "adventure" -> this.selectedGameMode = "survival";
                default -> this.selectedGameMode = "spectator";
            }

            this.updateButtonText();
        } else if (button.id == 103) {
            this.allowCommands = !this.allowCommands;
            this.updateButtonText();
        } else if (button.id == 101) {
            this.mc.displayGuiScreen(null);
            String lanInfo = this.mc.getIntegratedServer().shareToLAN(WorldSettings.GameType.getByName(this.selectedGameMode), this.allowCommands);
            IChatComponent chatComponent;

            if (lanInfo != null) {
                chatComponent = new ChatComponentTranslation("commands.publish.started", lanInfo);
            } else {
                chatComponent = new ChatComponentText("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("lanServer.title"), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}