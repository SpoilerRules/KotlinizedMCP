package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.LocalizationHelper;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.IDN;
import java.util.function.Predicate;

public class GuiScreenAddServer extends GuiScreen {
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private GuiTextField serverIPField;
    private GuiTextField serverNameField;
    private GuiButton serverResourcePacks;
    private final Predicate<String> ipAddressValidator = input -> {
        if (input.isEmpty()) {
            return true;
        } else {
            String[] addressComponents = input.split(":");

            if (addressComponents.length == 0) {
                return true;
            } else {
                try {
                    IDN.toASCII(addressComponents[0]);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        }
    };

    public GuiScreenAddServer(GuiScreen parentScreen, ServerData serverData) {
        this.parentScreen = parentScreen;
        this.serverData = serverData;
    }

    public void updateScreen() {
        this.serverNameField.updateCursorCounter();
        this.serverIPField.updateCursorCounter();
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, LocalizationHelper.translate("addServer.add")));
        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 18, LocalizationHelper.translate("gui.cancel")));
        buttonList.add(this.serverResourcePacks = new GuiButton(2, this.width / 2 - 100, this.height / 4 + 72, LocalizationHelper.translate("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()));
        this.serverNameField = new GuiTextField(0, this.fontRendererObject, this.width / 2 - 100, 66, 200, 20);
        this.serverNameField.setFocus(true);
        this.serverNameField.setText(this.serverData.serverName);
        this.serverIPField = new GuiTextField(1, this.fontRendererObject, this.width / 2 - 100, 106, 200, 20);
        this.serverIPField.setMaxStringLength(128);
        this.serverIPField.setText(this.serverData.serverIP);
        this.serverIPField.setValidator(this.ipAddressValidator);
        buttonList.get(0).enabled = !this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                this.serverData.setResourceMode(ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1) % ServerData.ServerResourceMode.values().length]);
                this.serverResourcePacks.displayString = LocalizationHelper.translate("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText();
            } else if (button.id == 1) {
                this.parentScreen.confirmClicked(false, 0);
            } else if (button.id == 0) {
                this.serverData.serverName = this.serverNameField.getText();
                this.serverData.serverIP = this.serverIPField.getText();
                this.parentScreen.confirmClicked(true, 0);
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.serverNameField.textboxKeyTyped(typedChar, keyCode);
        this.serverIPField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 15) {
            this.serverNameField.setFocus(!this.serverNameField.isFocused());
            this.serverIPField.setFocus(!this.serverIPField.isFocused());
        }

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(buttonList.get(0));
        }

        buttonList.get(0).enabled = !this.serverIPField.getText().isEmpty() && this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObject, LocalizationHelper.translate("addServer.title"), this.width / 2, 17, 16777215);
        this.drawString(this.fontRendererObject, LocalizationHelper.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.fontRendererObject, LocalizationHelper.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.serverNameField.drawTextBox();
        this.serverIPField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}