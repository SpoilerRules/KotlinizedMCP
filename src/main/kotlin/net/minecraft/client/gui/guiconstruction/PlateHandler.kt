package net.minecraft.client.gui.guiconstruction

import net.minecraft.client.settings.GameSettings

open class PlateHandler {
    data class OptionButton(val option: GameSettings.Options, val x: Int, val y: Int)
    data class CustomRedirectButton(
        val buttonID: Int,
        val x: Int,
        val y: Int,
        val buttonWidth: Int,
        val buttonHeight: Int,
        val buttonText: String
    )
    data class ClassicRedirectButton(val buttonID: Int, val x: Int, val y: Int, val buttonText: String)

    protected val createOptionButton = { option: GameSettings.Options, x: Int, y: Int -> OptionButton(option, x, y) }

    protected val createCustomRedirectButton =
        { buttonID: Int, x: Int, y: Int, buttonWidth: Int, buttonHeight: Int, buttonText: String ->
            CustomRedirectButton(
                buttonID,
                x,
                y,
                buttonWidth,
                buttonHeight,
                buttonText
            )
        }

    protected val createClassicRedirectButton =
        { buttonID: Int, x: Int, y: Int, buttonText: String -> ClassicRedirectButton(buttonID, x, y, buttonText) }
}