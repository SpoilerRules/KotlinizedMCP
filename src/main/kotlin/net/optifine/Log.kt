package net.optifine

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Log {
    private val LOG: Logger = LogManager.getLogger()
    public var logDetail = System.getProperty("log.detail", "false").equals("true")

    public fun detail(var s: String) {
        if (logDetail) {
            LOG.info("[OptiFine] " + s)
        }
    }

    public fun dbg(var s: String) {
        LOG.info("[OptiFine] " + s)
    }

    public fun warn(var s: String) {
        LOG.warn("[OptiFine] " + s)
    }

    public fun warn(var s: String, var t: Throwable) {
        LOG.warn("[OptiFine] " + s, t)
    }

    public fun error(var s: String) {
        LOG.error("[OptiFine] " + s)
    }

    public fun error(var s: String, var t: Throwable) {
        LOG.error("[OptiFine] " + s, t)
    }

    public fun log(var s: String) {
        dbg(s)
    }
}
