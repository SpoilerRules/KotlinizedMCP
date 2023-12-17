package net.minecraft.client

import net.minecraft.client.gui.GuiMemoryErrorScreen
import net.minecraft.crash.CrashReport
import net.minecraft.util.MinecraftError
import net.minecraft.util.ReportedException
import org.lwjgl.opengl.Display
import kotlin.system.exitProcess

class GameInitializer : CommonGameElement() {
    val handleException = { exception: Throwable ->
        val crashReport = mc.addGraphicsAndWorldToCrashReport(CrashReport("Unexpected error", exception))
        logger.fatal("Unreported exception thrown!", exception)
        mc.displayCrashReport(crashReport)
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

    }

    private fun initializeGameResources() {
        TODO()
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