package net.minecraft.client.gui.guimainmenu

import annotations.ExperimentalState
import annotations.Preview
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.optifine.CustomPanorama
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Project

object BackgroundRenderer : Gui() {
    private val mc = Minecraft.getMinecraft()
    private val largeTessellator = Tessellator(2097152)
    private val worldRenderer = largeTessellator.worldRenderer

    private val panoramaBackgroundTextures = Array(6) { index ->
        ResourceLocation("textures/gui/title/background/panorama_$index.png")
    }

    private var viewportTexture = DynamicTexture(256, 256)
    private var backgroundTexture = mc.textureManager.getDynamicTextureLocation("background", viewportTexture)

    private const val NUMBER_OF_LAYERS = 3

    fun initializePanorama(width: Int, height: Int, panoramaTimer: Int, partialTicks: Float) {
        GlStateManager.disableAlpha()

        mc.framebuffer.unbindFramebuffer()
        GlStateManager.viewport(0, 0, 256, 256)

        drawPanorama(panoramaTimer, partialTicks)

        repeat(7) {
            implementPanoramaTexture(width, height)
        }

        mc.framebuffer.bindFramebuffer(true)
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight)

        val scaleFactor = if (width > height) 120.0f / width.toFloat() else 120.0f / height.toFloat()
        val halfScaleX = width.toDouble() * scaleFactor / 256.0
        val halfScaleY = height.toDouble() * scaleFactor / 256.0

        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)

        val positions = listOf(
            Pair(0.0, height.toDouble()),
            Pair(width.toDouble(), height.toDouble()),
            Pair(width.toDouble(), 0.0),
            Pair(0.0, 0.0)
        )

        val texCoords = listOf(
            Pair((0.5 - halfScaleY), (0.5 + halfScaleX)),
            Pair((0.5 - halfScaleY), (0.5 - halfScaleX)),
            Pair((0.5 + halfScaleY), (0.5 - halfScaleX)),
            Pair((0.5 + halfScaleY), (0.5 + halfScaleX))
        )

        for (i in positions.indices) {
            worldRenderer.pos(positions[i].first, positions[i].second, zLevel.toDouble())
                .tex(texCoords[i].first, texCoords[i].second)
                .color(1.0f, 1.0f, 1.0f, 1.0f).endVertex()
        }

        largeTessellator.draw()

        GlStateManager.enableAlpha()
    }

    private fun implementPanoramaTexture(width: Int, height: Int) {
        mc.textureManager.bindTexture(backgroundTexture)

        applyPanoramaTextureLowQuality()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.colorMask(true, true, true, false)

        drawVertices(width, height)
        GlStateManager.enableAlpha()

        GlStateManager.colorMask(true,true,true,true)
    }

    @ExperimentalState // (I am not sure about this works correctly or not)
    private fun drawVertices(windowWidth: Int, windowHeight: Int) {
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        GlStateManager.disableAlpha()

        for (layerIndex in 0 until NUMBER_OF_LAYERS) {
            val transparency = 1.0f / (layerIndex + 1).toFloat()
            val textureOffset = (layerIndex - NUMBER_OF_LAYERS / 2).toFloat() / 256.0f

            val positions = listOf(
                Pair(windowWidth.toDouble(), windowHeight.toDouble()),
                Pair(windowWidth.toDouble(), 0.0),
                Pair(0.0, 0.0),
                Pair(0.0, windowHeight.toDouble())
            )

            val texCoords = listOf(
                Pair((0.0f + textureOffset).toDouble(), 1.0),
                Pair((1.0f + textureOffset).toDouble(), 1.0),
                Pair((1.0f + textureOffset).toDouble(), 0.0),
                Pair((0.0f + textureOffset).toDouble(), 0.0)
            )

            for (i in positions.indices) {
                worldRenderer.pos(positions[i].first, positions[i].second, zLevel.toDouble())
                    .tex(texCoords[i].first, texCoords[i].second)
                    .color(1.0f, 1.0f, 1.0f, transparency).endVertex()
            }
        }

        largeTessellator.draw()
    }

    private fun applyPanoramaTextureLowQuality() {
        val texture2D = GL11.GL_TEXTURE_2D
        val minFilter = GL11.GL_TEXTURE_MIN_FILTER
        val magFilter = GL11.GL_TEXTURE_MAG_FILTER
        val linear = GL11.GL_LINEAR

        GL11.glTexParameteri(texture2D, minFilter, linear)
        GL11.glTexParameteri(texture2D, magFilter, linear)
        GL11.glCopyTexSubImage2D(texture2D, 0, 0, 0, 0, 0, 256, 256)
    }

    @Preview
    private fun drawPanorama(panoramaTimer: Int, partialTicks: Float) {
        val projectionMatrix = GL11.GL_PROJECTION
        val modelViewMatrix = GL11.GL_MODELVIEW
        val srcAlpha = GL11.GL_SRC_ALPHA
        val oneMinusSrcAlpha = GL11.GL_ONE_MINUS_SRC_ALPHA
        val one = GL11.GL_ONE
        val zero = GL11.GL_ZERO

        GlStateManager.matrixMode(projectionMatrix)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f)
        GlStateManager.matrixMode(modelViewMatrix)
        GlStateManager.pushMatrix()
        GlStateManager.loadIdentity()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.disableCull()
        GlStateManager.depthMask(false)
        GlStateManager.tryBlendFuncSeparate(srcAlpha, oneMinusSrcAlpha, one, zero)

        val numTiles = 8
        for (tileIndex in 0 until numTiles * numTiles) {
            GlStateManager.pushMatrix()
            val tileX = ((tileIndex % numTiles).toFloat() / numTiles.toFloat() - 0.5f) / 64.0f
            val tileY = ((tileIndex / numTiles).toFloat() / numTiles.toFloat() - 0.5f) / 64.0f
            val tileZ = 0.0f
            GlStateManager.translate(tileX, tileY, tileZ)
            val rotation1 = kotlin.math.sin((panoramaTimer.toFloat() + partialTicks) / 400.0f) * 25.0f + 20.0f
            val rotation2 = -(panoramaTimer.toFloat() + partialTicks) * 0.1f
            GlStateManager.rotate(rotation1, 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(rotation2, 0.0f, 1.0f, 0.0f)

            for (faceIndex in 0..5) {
                GlStateManager.pushMatrix()
                when (faceIndex) {
                    1 -> GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f)
                    2 -> GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)
                    3 -> GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f)
                    4 -> GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f)
                    5 -> GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f)
                }
                mc.textureManager.bindTexture(panoramaBackgroundTextures[faceIndex])
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
                val alpha = 255 / (tileIndex + 1)
                worldRenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, alpha).endVertex()
                worldRenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, alpha).endVertex()
                worldRenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, alpha).endVertex()
                worldRenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, alpha).endVertex()
                largeTessellator.draw()
                GlStateManager.popMatrix()
            }
            GlStateManager.popMatrix()
            GlStateManager.colorMask(true, true, true, false)
        }
        worldRenderer.setTranslation(0.0, 0.0, 0.0)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.matrixMode(projectionMatrix)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(modelViewMatrix)
        GlStateManager.popMatrix()
        GlStateManager.depthMask(true)
        GlStateManager.enableCull()
        GlStateManager.enableDepth()
    }

    fun applyExtraPanoramaBlur(width: Int, height: Int) {
        val customPanoramaProperties = CustomPanorama.getCustomPanoramaProperties()

        val overlays = listOf(
            Pair(customPanoramaProperties?.overlay1Top ?: -2130706433, customPanoramaProperties?.overlay1Bottom ?: 16777215),
            Pair(customPanoramaProperties?.overlay2Top ?: 0, customPanoramaProperties?.overlay2Bottom ?: Int.MIN_VALUE)
        )

        overlays.forEach { (top, bottom) ->
            if (top != 0 || bottom != 0) {
                drawGradientRect(0, 0, width, height, top, bottom)
                return@forEach
            }
        }
    }
}