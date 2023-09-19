package net.minecraft.client.inputhandler

import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

object KeypressDispatcher {
    lateinit var currentScreen: GuiScreen

    val pressedKey = Keyboard.getEventKey().let { eventKey ->
        if (eventKey == 0) Keyboard.getEventCharacter() else eventKey
    }

    fun dispatchKeypresses() {
        if (pressedKey != 0 && !Keyboard.isRepeatEvent() && (currentScreen !is GuiControls || (currentScreen as GuiControls).time <= System.nanoTime() / 1000000L - 20L)) {
            if (Keyboard.getEventKeyState()) {

            }
        }
    }
}