package net.minecraft.client

import net.minecraft.util.Util
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class CommonGameElement : CommonResourceElement() {
    protected val logger: Logger = LogManager.getLogger()
    protected val mc: Minecraft = Minecraft.getMinecraft()
    protected val currentOS: Util.EnumOS = Util.getOSType()
}