package net.minecraft.client.gui.guimainmenu

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraft.util.client.ClientBrandEnum

class GuiMainMenu : GuiScreen() {
    private val titleMinecraftTexture = ResourceLocation("textures/gui/title/minecraft.png")

    companion object {
        var panoramaRotationTimer = 0
    }

    init {
        SplashTextService.initializeSplashTexts()
    }

    override fun updateScreen() {
        ++panoramaRotationTimer
    }

    override fun doesGuiPauseGame(): Boolean = false

    override fun initGui() {
        SplashTextService.applySplashText()

        ButtonHandler.initializeButtons(centerHeight, centerWidth)
    }

    override fun actionPerformed(button: GuiButton) = ButtonHandler.handleButtonClick(button, this, mc)

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        BackgroundRenderer.initializePanorama(width, height, panoramaRotationTimer, partialTicks)
        BackgroundRenderer.applyExtraPanoramaBlur(width, height)

        mc.textureManager.bindTexture(titleMinecraftTexture)

        SplashTextService.drawSplashText(fontRendererObject, width)

        drawString(fontRendererObject, ClientBrandEnum.MINECRAFT_VERSION,2,height - 10, -1)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}
