package net.minecraft.util.input

import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display

class MiceHelper {
    @JvmField var deltaX = 0
    @JvmField var deltaY = 0

    fun captureMouseCursor() {
        Mouse.setGrabbed(true)
        deltaX = 0
        deltaY = 0
    }

    fun releaseMouseCursor() {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2)
        Mouse.setGrabbed(false)
    }

    fun updateMouseDelta() {
        deltaX = Mouse.getDX()
        deltaY = Mouse.getDY()
    }
}