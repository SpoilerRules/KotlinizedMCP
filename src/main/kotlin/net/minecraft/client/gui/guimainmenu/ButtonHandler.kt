package net.minecraft.client.gui.guimainmenu

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonLanguage
import net.minecraft.client.gui.GuiLanguage
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSelectWorld
import net.minecraft.client.gui.mainMenuGui.microsoftLogin.LoginHandler
import net.minecraft.client.resources.LocalizationHelper

object ButtonHandler {
    enum class ButtonID(val id: Int) {
        OPTIONS_MENU("Options Menu".hashCode()),
        QUIT_GAME("Quit Game".hashCode()),
        SINGLE_PLAYER("Single Player".hashCode()),
        MULTI_PLAYER("Multi Player".hashCode()),
        LANGUAGE_SELECTION("Language Selection".hashCode()),
        MICROSOFT_LOGIN("Microsoft Login".hashCode());
    }

    data class ButtonPosition(val buttonID: ButtonID, val x: Int, val y: Int, val buttonWidth: Int, val buttonHeight: Int, val buttonText: String)
    data class SimpleButtonPosition(val buttonID: ButtonID, val x: Int, val y: Int, val buttonText: String)
    data class LanguageButtonPosition(val buttonID: ButtonID, val x: Int, val y: Int)

    fun initializeButtons(centerHeight: Int, centerWidth: Int) {
        val expectedVerticalHeight = centerHeight / 2 + 54
        val buttonList = GuiScreen.buttonList

        val clientButtons = listOf(
            ButtonPosition(ButtonID.OPTIONS_MENU, centerWidth - 100, expectedVerticalHeight + 84, 98, 20, LocalizationHelper.translate("menu.options")),
            ButtonPosition(ButtonID.QUIT_GAME, centerWidth + 2, expectedVerticalHeight + 84, 98, 20, LocalizationHelper.translate("menu.quit"))
        )

        val playButtons = listOf(
            SimpleButtonPosition(ButtonID.SINGLE_PLAYER, centerWidth - 100, expectedVerticalHeight, LocalizationHelper.translate("menu.singleplayer")),
            SimpleButtonPosition(ButtonID.MULTI_PLAYER, centerWidth - 100, expectedVerticalHeight + 24, LocalizationHelper.translate("menu.multiplayer"))
        )

        val additionalButtons = listOf(
            LanguageButtonPosition(ButtonID.LANGUAGE_SELECTION, centerWidth - 124, expectedVerticalHeight + 84),
            SimpleButtonPosition(ButtonID.MICROSOFT_LOGIN, centerWidth - 100, expectedVerticalHeight + 24 * 2, "Microsoft Login")
        )

        buttonList.run {
            clientButtons.forEach { (buttonID, x, y, buttonWidth, buttonHeight, buttonText) ->
                add(GuiButton(buttonID.id, x, y, buttonWidth, buttonHeight, buttonText))
            }

            playButtons.forEach { (buttonID, x, y, buttonText) ->
                add(GuiButton(buttonID.id, x, y, buttonText))
            }

            additionalButtons.forEach { position ->
                when (position) {
                    is LanguageButtonPosition -> add(GuiButtonLanguage(position.buttonID.id, position.x, position.y))
                    is SimpleButtonPosition -> add(GuiButton(position.buttonID.id, position.x, position.y, position.buttonText))
                }
            }
        }
    }

    fun handleButtonClick(button: GuiButton?, parentScreenInstance: GuiMainMenu, mc: Minecraft) {
        when (button?.id) {
            ButtonID.OPTIONS_MENU.id -> mc.displayGuiScreen(GuiOptions(parentScreenInstance, mc.gameSettings))
            ButtonID.QUIT_GAME.id -> mc.shutdown()
            ButtonID.SINGLE_PLAYER.id -> mc.displayGuiScreen(GuiSelectWorld(parentScreenInstance))
            ButtonID.MULTI_PLAYER.id -> mc.displayGuiScreen(GuiMultiplayer(parentScreenInstance))
            ButtonID.LANGUAGE_SELECTION.id -> mc.displayGuiScreen(GuiLanguage(parentScreenInstance, mc.gameSettings, mc.languageManager))
            ButtonID.MICROSOFT_LOGIN.id -> LoginHandler.initiateLogin()
        }
    }
}