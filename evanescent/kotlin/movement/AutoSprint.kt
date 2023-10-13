package movement

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings.Options.AUTO_SPRINT
import net.minecraft.util.Timer

class AutoSprint {
    fun sprint() = Minecraft.getMinecraft().thePlayer.run {
        if (!isSprinting && Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(AUTO_SPRINT)) isSprinting = true
        //sendChatMessage("timer: ${Timer(20f)}")
    }
}