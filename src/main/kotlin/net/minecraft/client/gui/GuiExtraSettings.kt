package net.minecraft.client.gui

import net.minecraft.client.resources.LocalizationHelper
import net.minecraft.client.settings.GameSettings

class GuiExtraSettings(private val parentScreen: GuiScreen) : GuiScreen() {

    override fun initGui() {
        val centerWidth = width / 2
        val centerHeight = height / 2
        val autoSprintOption: GameSettings.Options = GameSettings.Options.AUTO_SPRINT
     //   val rawInputOption: GameSettings.Options = GameSettings.Options.RAW_INPUT
        val keyBinding: String = mc.gameSettings.getKeyBinding(GameSettings.Options.AUTO_SPRINT)

        // "Done" button
        buttonList.run {
            add(
                GuiButton(
                    1,
                    centerWidth - 100,
                    centerHeight + 50,
                    LocalizationHelper.translate("gui.done")
                )
            )

            // "Automatic Sprint" button (middle)
            add(
                GuiOptionButton(
                    autoSprintOption.returnEnumOrdinal(),
                    centerWidth - 75,
                    centerHeight,
                    autoSprintOption,
                    keyBinding
                )
            )

            // "Raw Input" button
            add(
                GuiOptionButton(
                    autoSprintOption.returnEnumOrdinal(),
                    centerWidth - 75,
                    centerHeight,
                    autoSprintOption,
                    keyBinding
                )
            )
        }

        // "Camera Shake" button

        // "Jump Delay" button

        // "Scoreboard Numbers" button

        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) return

        when (button.id) {
            1 -> mc.displayGuiScreen(parentScreen)
            in 0 until 100 -> (button as? GuiOptionButton)?.let {
                mc.gameSettings.setOptionValue(it.returnEnumOptions(), 1)
                it.displayString = mc.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(it.id))
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        drawCenteredString(fontRendererObject, "Extra Settings", width / 2, 15, 16777215)
    }
}

data class ButtonDataProvider(val id: Int, val text: String, val xOffset: Int, val yOffset: Int)