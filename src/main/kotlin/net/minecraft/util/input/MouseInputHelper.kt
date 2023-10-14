package net.minecraft.util.input

import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display

class MouseInputHelper {
    @JvmField var deltaX = 0
    @JvmField var deltaY = 0

    private val screenWidth by lazy { Display.getWidth() / 2 }
    private val screenHeight by lazy { Display.getHeight() / 2 }

    fun captureMouseCursor() {
        Mouse.setGrabbed(true)
        deltaX = 0
        deltaY = 0
    }

    fun releaseMouseCursor() {
        Mouse.setCursorPosition(screenWidth, screenHeight)
        Mouse.setGrabbed(false)
    }

    fun updateMouseDelta() {
        deltaX = Mouse.getDX()
        deltaY = Mouse.getDY()
    }
}