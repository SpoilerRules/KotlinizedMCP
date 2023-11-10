package net.minecraft.client.inputhandler

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import net.minecraft.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class InputService {
    protected val logger: Logger by lazy { LogManager.getLogger() }

    protected val mc: Minecraft = Minecraft.getMinecraft()
    protected val player: EntityPlayerSP? = mc.thePlayer
    protected val activeScreen: GuiScreen? = mc.currentScreen
    protected var currentItem: ItemStack? = player?.inventory?.getCurrentItem()
    protected val potentialTimeResolution = mc.systemTime - mc.systemTime

    companion object {
        fun beginHandlingKeyInput(basicKeypressOnly: Boolean) {
            val mouseInputHandler = MouseInputHandler()
            val keyboardInputHandler = KeyboardInputHandler()

            if (basicKeypressOnly) keyboardInputHandler.handleParticularKeypresses()
            else {
                keyboardInputHandler.handleAllKeypresses()
                mouseInputHandler.startHandlingMouseInput()
            }
        }
    }
}