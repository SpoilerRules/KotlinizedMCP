package net.minecraft.client

import annotations.ExperimentalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.client.gui.GuiMemoryErrorScreen
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.data.*
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.shader.Framebuffer
import net.minecraft.crash.CrashReport
import net.minecraft.util.MinecraftError
import net.minecraft.util.ReportedException
import net.minecraft.util.input.MiceHelper
import org.lwjgl.Sys
import org.lwjgl.opengl.Display
import kotlin.system.exitProcess

class GameInitializer : CommonGameElement() {
    val handleException = { exception: Throwable ->
        val crashReport = mc.addGraphicsAndWorldToCrashReport(CrashReport("Unexpected error", exception))
        logger.fatal("Unreported exception thrown!", exception)
        mc.displayCrashReport(crashReport)
    }

    private inline fun <reified T : IMetadataSection> IMetadataSerializer.registerMetadataSectionType(serializer: IMetadataSectionSerializer<T>) {
        registerMetadataSectionType(serializer, T::class.java)
    }

    companion object {
        var isGameRunning = false
    }

    fun runGame() {
        isGameRunning = true

        try {
            mc.startGame()
        } catch (gameInitError: Throwable) {
            logger.error("An error occurred while initializing the game:", gameInitError)
        }

        while (isGameRunning) {
            try {
                if (!mc.hasGameCrashed || mc.crashReport == null) {
                    try {
                        mc.runGameLoop()
                    } catch (memoryError: OutOfMemoryError) {
                        mc.displayGuiScreen(GuiMemoryErrorScreen())
                    }
                } else {
                    mc.displayCrashReport(mc.crashReport)
                }
            } catch (minecraftError: MinecraftError) {
                logger.error("A suspicious error occurred while attempting to run the game loop", minecraftError)
                break
            } catch (reportedException: ReportedException) {
                handleException(reportedException)
                break
            } catch (unexpectedError: Throwable) {
                handleException(unexpectedError)
                break
            }
        }

        shutdownGame()
    }

    private fun loadGame() {
        // rung ame loop
    }

    @ExperimentalState
    fun initializeGameResources() {
        logger.info("LWJGL Version: " + Sys.getVersion())

        mc.gameSettings = GameSettings(mc, mc.mcDataDir)

        GameDisplayHandler().initializeGameWindow()
        OpenGlHelper.initializeTextures()

        runBlocking(Dispatchers.IO) {
            listOf(
                launch { mc.defaultResourcePacks.add(mc.mcDefaultResourcePack) },
                launch { mc.mouseHelper = MiceHelper() },
                launch {
                    val metadataSerializer = mc.metadataSerializer_

                    metadataSerializer.apply {
                        registerMetadataSectionType(TextureMetadataSectionSerializer())
                        registerMetadataSectionType(FontMetadataSectionSerializer())
                        registerMetadataSectionType(AnimationMetadataSectionSerializer())
                        registerMetadataSectionType(PackMetadataSectionSerializer())
                        registerMetadataSectionType(LanguageMetadataSectionSerializer())
                    }
                }
            ).forEach { it.join() }
        }

     /*   println("INITIALIZING RESOURCE PACK REPO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1")
        super.resourcePackRepository = ResourcePackRepository(
            mc.fileResourcepacks,
            File(mc.mcDataDir, "server-resource-packs"),
            mc.mcDefaultResourcePack,
            mc.metadataSerializer_,
            mc.gameSettings
        )

        mc.mcResourceManager = SimpleReloadableResourceManager(mc.metadataSerializer_)*/

        mc.framebufferMc = Framebuffer(mc.displayWidth, mc.displayHeight, true)
        mc.framebufferMc.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    private fun shutdownGame() {
        logger.info("Stopping!")

        try {
            mc.apply {
                loadWorld(null)
                soundHandler.unloadSounds()
            }
        } catch (exception: Exception) {
            logger.error("Error while shutting down game: ", exception)
            mc.hasGameCrashed = true
        } finally {
            Display.destroy()

            exitProcess(if (mc.hasGameCrashed) 1 else 0)
        }
    }
}