package net.minecraft.client.inputhandler

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

class DebugKeyProcessor : InputService() {
    internal fun processDebugKeys() {
        val keyCode = Keyboard.getEventKey()
        val keyState = Keyboard.getEventKeyState()

        KeyBinding.setKeyBindState(keyCode, keyState)

        if (!keyState) return

        KeyBinding.onTick(keyCode)

        when (keyCode) {
            1 -> mc.displayInGameMenu()
            59 -> mc.gameSettings.hideGUI = !mc.gameSettings.hideGUI
            62 -> mc.entityRenderer?.takeIf { true }?.switchUseShader()
            else -> {
                if (Keyboard.isKeyDown(61)) {
                    when (keyCode) {
                        20, 33 -> adjustRenderDistance(GuiScreen.isShiftKeyDown())
                        25 -> togglePauseOnLostFocus()
                        30 -> mc.renderGlobal.loadRenderers()
                        31 -> mc.refreshResources()
                        32 -> clearChatMessages()
                        35 -> toggleAdvancedItemTooltips()
                        48 -> toggleDebugBoundingBox()
                        61 -> toggleDebugInfo()
                    }
                }
            }
        }
    }

    private fun adjustRenderDistance(isShiftKeyDown: Boolean) {
        val renderDistanceChange = if (isShiftKeyDown) -1 else 1
        mc.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, renderDistanceChange)
    }

    private fun togglePauseOnLostFocus() {
        mc.gameSettings.pauseOnLostFocus = !mc.gameSettings.pauseOnLostFocus
        mc.gameSettings.saveOptions()
    }

    private fun clearChatMessages() = mc.ingameGUI?.chatGUI?.clearChatMessages()

    private fun toggleAdvancedItemTooltips() {
        mc.gameSettings.advancedItemTooltips = !mc.gameSettings.advancedItemTooltips
        mc.gameSettings.saveOptions()
    }

    private fun toggleDebugBoundingBox() = also {
        mc.renderManager.isDebugBoundingBox = !mc.renderManager.isDebugBoundingBox
    }

    private fun toggleDebugInfo() {
        mc.gameSettings.showDebugInfo = !mc.gameSettings.showDebugInfo
        mc.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown()
        mc.gameSettings.showLagometer = GuiScreen.isAltKeyDown()
    }
}
