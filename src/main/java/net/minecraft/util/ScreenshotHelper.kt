package net.minecraft.util

import java.io.File
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import kotlin.math.max

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import net.minecraft.event.ClickEvent
import net.minecraft.src.Config
import org.apache.logging.log4j.LogManager

class ScreenshotHelper {
    private val logger = LogManager.getLogger()
    private var pixelBuffer: IntBuffer
    private var pixelValues: IntArray

    init {
        val bufferSize = 1024
        pixelBuffer = BufferUtils.createIntBuffer(bufferSize)
        pixelValues = IntArray(bufferSize)
    }

    fun screenshotRequest(gameDirectory: File, width: Int, height: Int, buffer: Framebuffer): IChatComponent = saveScreenshot(gameDirectory, width, height, buffer)

    private fun saveScreenshot(
        gameDirectory: File,
        width: Int,
        height: Int,
        buffer: Framebuffer
    ): IChatComponent {
        try {
            val minecraft = Minecraft.getMinecraft()
            val originalGuiScale = Config.getGameSettings().guiScale
            val scaledResolution = ScaledResolution(minecraft)
            val scaleFactor = scaledResolution.scaleFactor
            val screenshotSize = Config.getScreenshotSize()
            val isFramebufferEnabled = OpenGlHelper.isFramebufferEnabled() && screenshotSize > 1

            if (isFramebufferEnabled) {
                Config.getGameSettings().guiScale = scaleFactor * screenshotSize
                resize(width * screenshotSize, height * screenshotSize)
                GlStateManager.pushMatrix()
                GlStateManager.clear(16640)
                minecraft.framebuffer.bindFramebuffer(true)
                minecraft.entityRenderer.updateCameraAndRender(Config.renderPartialTicks, System.nanoTime())
            }

            var finalWidth = width
            var finalHeight = height

            if (OpenGlHelper.isFramebufferEnabled()) {
                finalWidth = buffer.framebufferTextureWidth
                finalHeight = buffer.framebufferTextureHeight
            }

            val bufferSize = finalWidth * finalHeight

            if (pixelBuffer.capacity() < bufferSize) {
                pixelBuffer = BufferUtils.createIntBuffer(bufferSize)
                pixelValues = IntArray(bufferSize)
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            pixelBuffer.clear()

            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture)
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer)
            } else {
                GL11.glReadPixels(
                    0,
                    0,
                    finalWidth,
                    finalHeight,
                    GL12.GL_BGRA,
                    GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                    pixelBuffer
                )
            }

            pixelBuffer.get(pixelValues)
            TextureUtil.processPixelValues(pixelValues, finalWidth, finalHeight)
            val bufferedImage: BufferedImage

            if (OpenGlHelper.isFramebufferEnabled()) {
                bufferedImage =
                    BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, BufferedImage.TYPE_INT_ARGB)
                val yOffset = buffer.framebufferTextureHeight - buffer.framebufferHeight

                for (y in yOffset until buffer.framebufferTextureHeight) {
                    for (x in 0 until buffer.framebufferWidth) {
                        bufferedImage.setRGB(x, y - yOffset, pixelValues[y * buffer.framebufferTextureWidth + x])
                    }
                }
            } else {
                bufferedImage = BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB)
                bufferedImage.setRGB(0, 0, finalWidth, finalHeight, pixelValues, 0, finalWidth)
            }

            if (isFramebufferEnabled) {
                minecraft.framebuffer.unbindFramebuffer()
                GlStateManager.popMatrix()
                Config.getGameSettings().guiScale = originalGuiScale
                resize(finalWidth, finalHeight)
            }

            val screenshotFile = getTimestampedPNGFileForDirectory(File(gameDirectory, "screenshots").apply { mkdir() })
            val canonicalScreenshotFile = screenshotFile.canonicalFile

            ImageIO.write(bufferedImage, "png", canonicalScreenshotFile)
            val chatComponent = ChatComponentText(canonicalScreenshotFile.name)
            chatComponent.chatStyle.chatClickEvent =
                ClickEvent(ClickEvent.Action.OPEN_FILE, canonicalScreenshotFile.absolutePath)
            chatComponent.chatStyle.setUnderlined(true)

            return ChatComponentTranslation("screenshot.success", chatComponent)
        } catch (exception: Exception) {
            logger.warn("Couldn't save screenshot", exception)
            val errorMessage = exception.message ?: "Unknown error"
            return ChatComponentTranslation("screenshot.failure", errorMessage)
        }
    }

    private fun getTimestampedPNGFileForDirectory(gameDirectory: File): File {
        val date = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(Date())
        return generateSequence(1) { it + 1 }
            .map { File(gameDirectory, "$date${if (it == 1) "" else "_$it"}.png") }
            .first { !it.exists() }
    }

    private fun resize(newWidth: Int, newHeight: Int) {
        val minecraft = Minecraft.getMinecraft()
        minecraft.displayWidth = max(1, newWidth)
        minecraft.displayHeight = max(1, newHeight)

        minecraft.currentScreen?.run {
            val scaledResolution = ScaledResolution(minecraft)
            onResize(minecraft, scaledResolution.scaledWidth, scaledResolution.scaledHeight)
        }

        updateFramebufferSize()
    }

    private fun updateFramebufferSize() {
        val minecraft = Minecraft.getMinecraft()
        with(minecraft.framebuffer) {
            createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight)
        }
        minecraft.entityRenderer?.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight)
    }
}