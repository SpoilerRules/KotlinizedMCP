package net.minecraft.client.inputhandler

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ScreenshotHandler
import org.lwjgl.input.Keyboard

object KeyboardInputHandler {
    fun dispatchKeypresses(mc: Minecraft) {
        val flag = mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
        val currentScreen: GuiScreen? = Minecraft.getMinecraft().currentScreen

        val i = if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter() else Keyboard.getEventKey()

        if (i != 0 && !Keyboard.isRepeatEvent()) {
            if (currentScreen !is GuiControls || currentScreen.time <= System.currentTimeMillis() - 20L) {
                if (Keyboard.getEventKeyState()) {
                    when (i) {
                        mc.gameSettings.keyBindFullscreen.keyCode -> mc.toggleFullscreen()
                        mc.gameSettings.keyBindScreenshot.keyCode -> mc.ingameGUI.chatGUI.printChatMessage(
                            ScreenshotHandler.takeScreenshot(
                                mc.mcDataDir,
                                mc.displayWidth,
                                mc.displayHeight,
                                mc.framebufferMc
                            )
                        )
                    }
                }
            }
        }
    }
}