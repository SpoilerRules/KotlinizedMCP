package movement

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings.Options.AUTO_SPRINT
import net.minecraft.client.settings.KeyBinding

class AutoSprint {
    private val mc by lazy { Minecraft.getMinecraft() }

    fun sprint() = mc.run {
        if (!thePlayer.isSprinting && gameSettings.getOptionOrdinalValue(AUTO_SPRINT)) KeyBinding.setKeyBindState(
            gameSettings.keyBindSprint.keyCode,
            true
        )
    }
}