package net.minecraft.client.inputhandler

import annotations.ExperimentalState
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.util.ScreenshotHandler
import org.lwjgl.input.Keyboard

@ExperimentalState
class KeyboardInputHandler : InputService() {
    private val debugKeyProcessor = DebugKeyProcessor()

    companion object {
        private const val KEY_EVENT_ZERO = 0
    }

    fun handleParticularKeypresses() {
        val keyEvent = if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter() else Keyboard.getEventKey()

        if (keyEvent == KEY_EVENT_ZERO || Keyboard.isRepeatEvent() || !Keyboard.getEventKeyState()) return

        if (activeScreen == null || activeScreen.allowUserInput) {
            when (keyEvent) {
                mc.gameSettings.keyBindChat.keyCode -> mc.theWorld?.let {
                    mapOf(
                        EntityPlayer.EnumChatVisibility.FULL to { mc.displayGuiScreen(GuiChat()) },
                        EntityPlayer.EnumChatVisibility.SYSTEM to { mc.displayGuiScreen(GuiChat("/")) }
                    )[mc.gameSettings.chatVisibility]?.invoke()
                }

                mc.gameSettings.keyBindDrop.keyCode -> player?.takeUnless { it.isSpectator }
                    ?.dropOneItem(GuiScreen.isCtrlKeyDown())

                mc.gameSettings.keyBindInventory.keyCode -> {
                    if (mc.playerController.isRidingHorse) player?.sendHorseInventory()
                    else {
                        mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
                        mc.displayGuiScreen(GuiInventory(player))
                    }
                }
            }
        }

        if (activeScreen !is GuiControls) {
            when (keyEvent) {
                mc.gameSettings.keyBindFullscreen.keyCode -> mc.toggleFullscreen()
                mc.gameSettings.keyBindScreenshot.keyCode -> mc.ingameGUI.chatGUI.printChatMessage(
                    ScreenshotHandler.takeScreenshot(
                        mc.mcDataDir,
                        mc.displayWidth,
                        mc.displayHeight,
                        mc.framebufferMc
                    )
                )
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

