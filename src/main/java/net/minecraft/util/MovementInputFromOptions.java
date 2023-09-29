package net.minecraft.util;

import movement.AutoSprint;
import net.minecraft.client.settings.GameSettings;

// this class will be omitted when related classes are kotlinized. (pending sacrifice)
public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;
    private final AutoSprint autoSprint = new AutoSprint();

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        moveStrafe = moveForward = 0.0F;

        if (gameSettings.keyBindForward.isKeyDown()) {
            moveForward++;
            autoSprint.sprint();
        }

        if (gameSettings.keyBindBack.isKeyDown()) moveForward--;
        if (gameSettings.keyBindLeft.isKeyDown()) moveStrafe++;
        if (gameSettings.keyBindRight.isKeyDown()) moveStrafe--;

        jump = gameSettings.keyBindJump.isKeyDown();
        sneak = gameSettings.keyBindSneak.isKeyDown();

        if (sneak) {
            moveStrafe *= 0.3F;
            moveForward *= 0.3F;
        }
    }
}