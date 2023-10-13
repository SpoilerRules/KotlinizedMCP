package net.minecraft.util

import net.minecraft.client.Minecraft

class Timer(private var ticksPerSecond: Float) {
    companion object {
        private const val NANO_TO_MILLIS = 1_000_000L
        private const val MILLIS_TO_SECS = 1_000.0
        private const val TIMER_SPEED_ADJUSTMENT_FACTOR = 0.20000000298023224
        private const val MAX_ELAPSED_TICKS = 10
    }

    private var lastHRTime: Double = 0.0
    var elapsedTicks: Int = 0
    var renderPartialTicks: Float = 0.0f
    private var timerSpeed: Float = 1.0f
    private var elapsedPartialTicks: Float = 0.0f
    private var lastSyncSysClock: Long
    private var lastSyncHRClock: Long
    private var counter: Long = 0L
    // For developers: change the variable below to modify timer speed of the world.
    private var timerSpeedAdjustment: Double = 1.0

    init {
        Minecraft.getSystemTime().also { lastSyncSysClock = it }
        (System.nanoTime() / NANO_TO_MILLIS).also { lastSyncHRClock = it }
    }

    fun updateTimer() {
        val currentTimeMillis = Minecraft.getSystemTime()
        val timeDifferenceMillis = currentTimeMillis - lastSyncSysClock
        val nanoTimeMillis = System.nanoTime() / NANO_TO_MILLIS
        val nanoTimeSecs = nanoTimeMillis / MILLIS_TO_SECS

        if (timeDifferenceMillis in 0L..1000L) {
            counter += timeDifferenceMillis

            if (counter > 1000L) {
                val hrClockDifference = nanoTimeMillis - lastSyncHRClock
                val timeRatio = counter.toDouble() / hrClockDifference.toDouble()
                timerSpeedAdjustment += (timeRatio - timerSpeedAdjustment) * TIMER_SPEED_ADJUSTMENT_FACTOR
                lastSyncHRClock = nanoTimeMillis
                counter = 0L
            }

            if (counter < 0L) {
                lastSyncHRClock = nanoTimeMillis
            }
        } else {
            lastHRTime = nanoTimeSecs
        }

        lastSyncSysClock = currentTimeMillis

        val timeAdjustment = (nanoTimeSecs - lastHRTime) * timerSpeedAdjustment
        lastHRTime = nanoTimeSecs

        val clampedAdjustment = timeAdjustment.coerceIn(0.0, 1.0)
        elapsedPartialTicks += (clampedAdjustment * timerSpeed * ticksPerSecond).toFloat()
        elapsedTicks = elapsedPartialTicks.toInt()
        elapsedPartialTicks -= elapsedTicks.toFloat()

        if (elapsedTicks > MAX_ELAPSED_TICKS) {
            elapsedTicks = MAX_ELAPSED_TICKS
        }

        renderPartialTicks = elapsedPartialTicks
    }
}