package net.minecraft.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.shader.Framebuffer
import net.minecraft.event.ClickEvent

import org.apache.logging.log4j.LogManager
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.IntBuffer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.imageio.ImageIO

object ScreenshotHandler {
    private val logger by lazy { LogManager.getLogger() }
    private val screenshotDateFormat: DateFormat by lazy { SimpleDateFormat("yyyy-MM-dd_HH.mm.ss") }

    private var pixelBuffer: IntBuffer? = null
    private lateinit var pixelValues: IntArray

    @OptIn(DelicateCoroutinesApi::class)
    fun takeScreenshot(gameDirectory: File, width: Int, height: Int, framebuffer: Framebuffer): IChatComponent {
        val (screenWidth, screenHeight) = if (OpenGlHelper.isFramebufferEnabled()) {
            Pair(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight)
        } else Pair(width, height)

        return try {
            val screenshotDir = createScreenshotDirectory(gameDirectory)
            val pixelCount = screenWidth * screenHeight

            initializePixelBuffer(pixelCount)
            readPixelsIntoBuffer(screenWidth, screenHeight, framebuffer)

            val screenshotFile = generateTimestampedPNGFile(screenshotDir)
            GlobalScope.launch {
                val screenshotImage = createScreenshotImage(screenWidth, screenHeight, framebuffer)
                saveScreenshotImage(screenshotImage, screenshotFile)
            }

            val chatComponent = createChatComponent(screenshotFile)
            ChatComponentTranslation("screenshot.success", chatComponent)
        } catch (exception: Exception) {
            logger.warn("Couldn't save screenshot", exception)
            ChatComponentTranslation("screenshot.failure", exception.message)
        }
    }

    /**
     * Reads pixel data from the OpenGL framebuffer into a buffer for further processing.
     *
     * This function performs the necessary OpenGL calls to read pixel data from either the framebuffer
     * or the screen, depending on the context. It then stores the pixel data in the provided buffer
     * for later use.
     *
     * **Caution:** This code is considered delicate and requires careful handling. Avoid making changes without thorough debugging.
     *
     * @param width The width of the region to read pixels from.
     * @param height The height of the region to read pixels from.
     * @param framebuffer The framebuffer to capture (if OpenGL framebuffer is enabled).
     *
     * @throws IllegalStateException If OpenGL framebuffer is not enabled.
     */
    private fun readPixelsIntoBuffer(width: Int, height: Int, framebuffer: Framebuffer) {
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
        pixelBuffer?.clear()

        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(framebuffer.framebufferTexture)
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer)
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer)
        }

        pixelBuffer?.let { it[pixelValues] }
        TextureUtil.processPixelValues(pixelValues, width, height)
    }

    /**
     * Creates a screenshot image based on the specified parameters.
     *
     * This method captures the current framebuffer or the provided width and height and
     * produces a BufferedImage containing the screenshot image data.
     *
     * **Caution:** This code is considered delicate and may require careful handling. Avoid making changes without thorough debugging.
     *
     * @param width The width of the screenshot image.
     * @param height The height of the screenshot image.
     * @param framebuffer The framebuffer to capture (if OpenGL framebuffer is enabled).
     * @return A BufferedImage containing the screenshot image.
     *
     * @throws IllegalArgumentException If the provided width or height is non-positive.
     * @throws IllegalStateException If OpenGL framebuffer is not enabled.
     */
    private fun createScreenshotImage(width: Int, height: Int, framebuffer: Framebuffer): BufferedImage = if (OpenGlHelper.isFramebufferEnabled()) {
            BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1).apply {
                val yOffset = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight
                for (y in yOffset until framebuffer.framebufferTextureHeight) {
                    for (x in 0 until framebuffer.framebufferWidth) {
                        setRGB(x, y - yOffset, pixelValues[y * framebuffer.framebufferTextureWidth + x])
                    }
                }
            }
        } else BufferedImage(width, height, 1).apply {
        setRGB(0, 0, width, height, pixelValues, 0, width)
    }

    private fun createScreenshotDirectory(gameDirectory: File): File = File(gameDirectory, "screenshots").apply {
        if (!exists() && !mkdir()) {
            throw IOException("Failed to create screenshot directory.")
        }
    }

    private fun initializePixelBuffer(pixelCount: Int) {
        if (pixelBuffer == null || pixelBuffer!!.capacity() < pixelCount) {
            pixelBuffer = BufferUtils.createIntBuffer(pixelCount)
            pixelValues = IntArray(pixelCount)
        }
    }

    private fun saveScreenshotImage(screenshotImage: BufferedImage, screenshotFile: File) =
        runCatching {
            ImageIO.write(screenshotImage, "png", screenshotFile)
        }.onFailure { e ->
            logger.error("Failed to save screenshot image", e)
        }

    private fun generateTimestampedPNGFile(screenshotDir: File) = generateSequence(1) { it + 1 }
        .map { File(screenshotDir, "${screenshotDateFormat.format(Date())}${if (it == 1) "" else "_$it"}.png") }
        .first { !it.exists() }

    private fun createChatComponent(screenshotFile: File) = ChatComponentText(screenshotFile.name).apply {
        chatStyle = chatStyle.apply {
            chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotFile.absolutePath)
            underlined = true
        }
    }
}