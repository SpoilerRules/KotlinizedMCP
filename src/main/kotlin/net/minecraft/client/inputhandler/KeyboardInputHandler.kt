package net.minecraft.client.inputhandler

import annotations.ExperimentalState
import net.minecraft.client.GameDisplayHandler
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiContainerCreative
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.util.ScreenshotHandler
import org.lwjgl.input.Keyboard

@ExperimentalState
class KeyboardInputHandler : InputService() {
    private val debugKeyProcessor = DebugKeyProcessor()

    fun handleParticularKeypresses() {
        val keyEvent = if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter() else Keyboard.getEventKey()

        if (keyEvent == 0 || Keyboard.isRepeatEvent() || !Keyboard.getEventKeyState()) return

        if (activeScreen == null || activeScreen.allowUserInput) {
            when (keyEvent) {
                mc.gameSettings.keyBindDrop.keyCode -> player?.takeUnless { it.isSpectator }
                    ?.dropOneItem(GuiScreen.isCtrlKeyDown())

                mc.gameSettings.keyBindInventory.keyCode -> if (mc.playerController.isRidingHorse) {
                    if (activeScreen !is GuiScreenHorseInventory) player?.sendHorseInventory()
                } else {
                    if (activeScreen !is GuiInventory && activeScreen !is GuiContainerCreative) {
                        mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                        mc.displayGuiScreen(GuiInventory(player))
                    }
                }

                mc.gameSettings.keyBindChat.keyCode -> if (activeScreen !is GuiInventory && activeScreen !is GuiContainerCreative) mc.theWorld?.let {
                    mapOf(
                        EntityPlayer.EnumChatVisibility.FULL to { mc.displayGuiScreen(GuiChat()) },
                        EntityPlayer.EnumChatVisibility.SYSTEM to { mc.displayGuiScreen(GuiChat("/")) }
                    )[mc.gameSettings.chatVisibility]?.invoke()
                }
            }

            if (activeScreen !is GuiControls) {
                when (keyEvent) {
                    mc.gameSettings.keyBindFullscreen.keyCode -> GameDisplayHandler.switchFullscreenMode()
                    mc.gameSettings.keyBindScreenshot.keyCode -> {
                        val screenshotMessage = ScreenshotHandler.takeScreenshot(
                            mc.mcDataDir,
                            mc.displayWidth,
                            mc.displayHeight,
                            mc.framebufferMc
                        )
                        mc.ingameGUI.chatGUI.printChatMessage(screenshotMessage)
                    }
                }
            }
        }
    }

    fun handleAllKeypresses() {
        player ?: return
        activeScreen?.let { if (!it.allowUserInput) return }

        mc.mcProfiler.endStartSection("keyboard")

        handleHotbarSelection()

        if (mc.leftClickCounter > 0) mc.leftClickCounter--

        while (Keyboard.next()) {
            handleParticularKeypresses()
            debugKeyProcessor.processDebugKeys()

            mc.gameSettings.keyBindTogglePerspective.takeIf { it.isPressed }?.let {
                mc.gameSettings.thirdPersonView = (mc.gameSettings.thirdPersonView + 1) % 3
                mc.entityRenderer?.loadEntityShader(if (mc.gameSettings.thirdPersonView == 0) mc.renderViewEntity else null)
                mc.renderGlobal.setDisplayListEntitiesDirty()
            }

            if (mc.gameSettings.keyBindSmoothCamera.isPressed) mc.gameSettings.smoothCamera =
                !mc.gameSettings.smoothCamera
        }
    }

    private fun handleHotbarSelection() {
        (0..8).firstOrNull { mc.gameSettings.keyBindsHotbar[it].isPressed }?.let { hotbarIndex ->
            when (player?.isSpectator) {
                true -> mc.ingameGUI.spectatorGui.func_175260_a(hotbarIndex)
                else -> player?.inventory?.currentItem = hotbarIndex
            }
        }
    }
}

