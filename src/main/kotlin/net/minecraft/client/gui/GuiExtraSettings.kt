package net.minecraft.client.gui

import net.minecraft.client.resources.LocalizationHelper

class GuiExtraSettings(private val parentScreen: GuiScreen) : GuiScreen() {

    init {}

    override fun initGui() {
        buttonList.add(
            GuiButton(1, width / 2 - 100, height / 6 + 168 + 11, LocalizationHelper.translate("gui.done"))
        )

        super.initGui()
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.enabled) {
            when (button.id) {
                1 -> mc.displayGuiScreen(parentScreen)
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}