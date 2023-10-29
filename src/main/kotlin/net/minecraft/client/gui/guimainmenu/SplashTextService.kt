package net.minecraft.client.gui.guimainmenu

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.IResource
import net.minecraft.util.ResourceLocation
import java.util.Calendar
import java.util.Date
import kotlin.random.Random
import kotlin.math.*

object SplashTextService : Gui() {
    private val splashTextsResource = ResourceLocation("texts/splashes.txt")

    private var splashText: String? = null
    private var splashTextUpdateCounter = 0f

    fun initializeSplashTexts() {
        val resourceManager = Minecraft.getMinecraft().resourceManager ?: return

        val splashResource = resourceManager.getResource(splashTextsResource) ?: return

        processSplashTextResource(splashResource)
    }

    private fun processSplashTextResource(splashResource: IResource) {
        splashResource.inputStream.bufferedReader(Charsets.UTF_8).use { bufferedReader ->
            val splashTextList = bufferedReader.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (splashTextList.isNotEmpty()) {
                splashText = selectRandomSplashText(splashTextList)
                splashTextUpdateCounter = Random.nextFloat()
            }
        }
    }

    private fun selectRandomSplashText(splashTextList: List<String>): String =
        generateSequence { splashTextList.random() }
            .first { it.hashCode() != 125780783 }

    /**
     * Selects a special splash text based on a special date (e.g., holidays, birthdays) or falls back to a random splash text.
     * The function checks the current date and assigns an appropriate splash text based on specific date-based conditions.
     * If no special date condition is met, it retains the current splash text.
     *
     * @return The selected splash text, either based on a special date or randomly.
     */
    fun applySplashText() {
        val calendar = Calendar.getInstance().apply { time = Date() }
        val monthDatePair = Pair(calendar[Calendar.MONTH] + 1, calendar[Calendar.DATE])

        splashText = when (monthDatePair) {
            Pair(12, 7) -> "Happy birthday, Spoili!"
            Pair(12, 24) -> "Merry X-mas!"
            Pair(1, 1) -> "Happy new year!"
            Pair(10, 31) -> "OOoooOOOoooo! Spooky!"
            else -> splashText
        }
    }

    fun drawSplashText(fontRenderer: FontRenderer, screenWidth: Int) {
        val splashImageCenter = screenWidth / 2 - 137 to 30
        val textureRectangles = getTextureRectangles()

        drawTextureRectangles(splashImageCenter, textureRectangles)
        applyMatrixTransformations(screenWidth, fontRenderer)
    }

    private fun getTextureRectangles() = if (splashTextUpdateCounter < 1.0E-4) listOf(
        0 to listOf(0, 0, 99, 44),
        99 to listOf(129, 0, 27, 44),
        125 to listOf(126, 0, 3, 44),
        128 to listOf(99, 0, 26, 44),
        155 to listOf(0, 45, 155, 44)
    ) else listOf(
        0 to listOf(0, 0, 155, 44),
        155 to listOf(0, 45, 155, 44)
    )

    private fun drawTextureRectangles(splashImageCenter: Pair<Int, Int>, textureRectangles: List<Pair<Int, List<Int>>>) {
        textureRectangles.forEach { (xOffset, textureParams) ->
            drawTexturedModalRect(splashImageCenter.first + xOffset, splashImageCenter.second,
                textureParams[0], textureParams[1], textureParams[2], textureParams[3])
        }
    }

    /**
     * Applies matrix transformations to position and scale the splash text correctly on the screen and then draws it.
     *
     * @param screenWidth The width of the screen where the text is positioned.
     * @param fontRenderer The font renderer used for rendering the text.
     */
    private fun applyMatrixTransformations(screenWidth: Int, fontRenderer: FontRenderer) {
        GlStateManager.pushMatrix()
        GlStateManager.translate((screenWidth / 2 + 90).toFloat(), 70.0f, 0.0f)
        GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f)

        val textScaleFactor = calculateTextScaleFactor(fontRenderer)

        GlStateManager.scale(textScaleFactor, textScaleFactor, textScaleFactor)

        drawCenteredString(fontRenderer, splashText, 0, -8, -256)

        GlStateManager.popMatrix()
    }

    private fun calculateTextScaleFactor(fontRenderer: FontRenderer): Float =
        (1.8f - abs(sin((Minecraft.getSystemTime() % 1000L).toFloat() / 1000.0f * PI.toFloat() * 2.0f) * 0.1f)) * 100.0f / (fontRenderer.getStringWidth(splashText) + 32)
}