package movement

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings.Options.AUTO_SPRINT

class AutoSprint {
    fun sprint() = Minecraft.getMinecraft().thePlayer.run {
        if (!isSprinting && Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(AUTO_SPRINT)) isSprinting = true
    }
}