package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {
    private final GameSettings.Options gameOptions;

    public GuiOptionButton(int buttonId, int width, int height, String displayString) {
        this(buttonId, width, height, null, displayString);
    }

    public GuiOptionButton(int buttonId, int positionX, int positionY, int buttonWidth, int buttonHeight, String displayString) {
        super(buttonId, positionX, positionY, buttonWidth, buttonHeight, displayString);
        this.gameOptions = null;
    }

    public GuiOptionButton(int buttonId, int positionX, int positionY, GameSettings.Options gameOptions, String displayString) {
        super(buttonId, positionX, positionY, 150, 20, displayString);
        this.gameOptions = gameOptions;
    }

    public GameSettings.Options returnEnumOptions() {
        return this.gameOptions;
    }
}