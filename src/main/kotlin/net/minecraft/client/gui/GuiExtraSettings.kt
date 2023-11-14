package net.minecraft.client.gui

import net.minecraft.client.resources.LocalizationHelper
import net.minecraft.client.settings.GameSettings

class GuiExtraSettings(private val parentScreen: GuiScreen) : GuiScreen() {

    override fun initGui() {
        val centerHeight = super.centerHeight - 20

        val options = listOf(
            createOptionButton(GameSettings.Options.AUTO_SPRINT, centerWidth - 75, centerHeight + 15), // Correctly implemented.
            createOptionButton(GameSettings.Options.RAW_INPUT, centerWidth - 190, centerHeight - 75), // Not correctly implemented. Placeholder as in-use of current module (Mouse Delay Fix).
            createOptionButton(GameSettings.Options.CAMERA_SHAKE, centerWidth - 190, centerHeight - 30), // Not correctly implemented. Placeholder as in-use of current module (Hurt Shake).
            createOptionButton(GameSettings.Options.JUMP_DELAY, centerWidth + 50, centerHeight - 75), // Correctly Implemented.
            createOptionButton(GameSettings.Options.SCORE_DISPLAY, centerWidth + 50, centerHeight - 30) // Correctly Implemented.
        )

        buttonList.run {
            // "Done" button
            add(
                GuiButton(
                    1,
                    centerWidth - 100,
                    centerHeight + 50,
                    LocalizationHelper.translate("gui.done")
                )
            )

            // Option buttons
            options.forEach { (option, x, y) ->
                add(
                    GuiOptionButton(
                        option.returnEnumOrdinal(),
                        x,
                        y,
                        option,
                        mc.gameSettings.getKeyBinding(option)
                    )
                )
            }
        }

        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) return

        when (button.id) {
            1 -> mc.displayGuiScreen(parentScreen)
            in 0 until 100 ->  if (button is GuiOptionButton) {
                mc.gameSettings.setOptionValue(button.returnEnumOptions(), 1)
                button.displayString = mc.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id))
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        drawCenteredString(fontRendererObject, "Extra Settings", width / 2, 15, 16777215)
    }
}