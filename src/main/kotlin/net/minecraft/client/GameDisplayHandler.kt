package net.minecraft.client

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Util
import net.minecraft.util.client.ClientBrandEnum
import org.apache.logging.log4j.LogManager
import org.lwjgl.LWJGLException
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.PixelFormat
import java.io.IOException
import kotlin.math.max

class GameDisplayHandler : CommonGameElement() {
    companion object {
        val mc: Minecraft = Minecraft.getMinecraft()

        private val macScreenResolutions = listOf(
            DisplayMode(2560, 1600),
            DisplayMode(2880, 1800)
        )

        val updateDisplaySizeAndRender = { newWidth: Int, newHeight: Int ->
            mc.displayWidth = max(1, newWidth)
            mc.displayHeight = max(1, newHeight)

            mc.currentScreen?.let { screen ->
                val scaledResolution = ScaledResolution(mc)
                screen.onResize(mc, scaledResolution.scaledWidth, scaledResolution.scaledHeight)
            }

            mc.loadingScreen = LoadingScreenRenderer(mc)
            updateFramebufferAndShaderGroupSize()
        }

        val updateFramebufferAndShaderGroupSize = {
            mc.framebufferMc.createBindFramebuffer(mc.displayWidth, mc.displayHeight)
            mc.entityRenderer?.updateShaderGroupSize(mc.displayWidth, mc.displayHeight)
        }

        fun switchFullscreenMode() {
            val updateDisplayDimensions = { width: Int, height: Int ->
                mc.displayWidth = max(1, width)
                mc.displayHeight = max(1, height)
            }

            runCatching {
                mc.fullscreen = !mc.fullscreen
                mc.gameSettings.fullScreen = mc.fullscreen

                if (mc.fullscreen) {
                    setOptimalDisplayMode()
                    updateDisplayDimensions(Display.getDisplayMode().width, Display.getDisplayMode().height)
                } else {
                    Display.setDisplayMode(DisplayMode(mc.tempDisplayWidth, mc.tempDisplayHeight))
                    updateDisplayDimensions(mc.tempDisplayWidth, mc.tempDisplayHeight)
                }

                mc.currentScreen?.let { updateDisplaySizeAndRender(mc.displayWidth, mc.displayHeight) }
                    ?: updateFramebufferAndShaderGroupSize()

                Display.setFullscreen(mc.fullscreen)
                Display.setVSyncEnabled(mc.gameSettings.enableVsync)
                mc.updateDisplay()
            }.onFailure {
                LogManager.getLogger().error("Couldn't toggle fullscreen", it)
            }
        }

        private fun setOptimalDisplayMode() {
            val availableModes = Display.getAvailableDisplayModes().toHashSet()
            var desktopMode = Display.getDesktopDisplayMode()

            if (desktopMode !in availableModes && Util.getOSType() == Util.EnumOS.OSX) {
                macScreenResolutions.firstOrNull { targetMode ->
                    val isModeAvailable = availableModes.any {
                        it.bitsPerPixel == 32 && it.width == targetMode.width && it.height == targetMode.height
                    }

                    if (!isModeAvailable) {
                        availableModes.firstOrNull {
                            it.bitsPerPixel == 32 && it.width == targetMode.width / 2 && it.height == targetMode.height / 2
                        }?.let {
                            desktopMode = it
                            return@firstOrNull true
                        }
                    }
                    false
                }
            }

            Display.setDisplayMode(desktopMode)
            mc.displayWidth = desktopMode.width
            mc.displayHeight = desktopMode.height
        }
    }

    fun initializeGameWindow() {
        Display.setResizable(true)
        Display.setTitle(ClientBrandEnum.WINDOW_DISPLAY_TITLE)
        setWindowIcon()

        if (mc.fullscreen) {
            Display.setFullscreen(true)
            mc.displayWidth = max(1, Display.getDisplayMode().width)
            mc.displayHeight = max(1, Display.getDisplayMode().height)
        } else {
            Display.setDisplayMode(DisplayMode(mc.displayWidth, mc.displayHeight))
        }

        runCatching {
            Display.create(PixelFormat().withDepthBits(24))
        }.onFailure { e ->
            logger.error("Couldn't set pixel translate", e)
            if (mc.fullscreen) setOptimalDisplayMode()
        }

        check(Display.isCreated()) { "Failed to create display" }
    }

    private fun setWindowIcon() {
        if (Util.getOSType() == Util.EnumOS.OSX) return

        try {
            val smallIconStream =
                mc.mcDefaultResourcePack.getInputStreamAssets(ResourceLocation("icons/icon_16x16.png"))
            val largeIconStream =
                mc.mcDefaultResourcePack.getInputStreamAssets(ResourceLocation("icons/icon_32x32.png"))

            smallIconStream?.let { smallStream ->
                largeIconStream?.let { largeStream ->
                    Display.setIcon(arrayOf(mc.readImageToBuffer(smallStream), mc.readImageToBuffer(largeStream)))
                }
            }
        } catch (e: IOException) {
            logger.error("Failed to set window icon.", e)
        }
    }
}