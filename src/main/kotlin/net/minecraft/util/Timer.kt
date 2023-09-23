package net.minecraft.util

import net.minecraft.client.Minecraft

class Timer(private var ticksPerSecond: Float) {
    private var lastHRTime: Double = 0.0
    var elapsedTicks: Int = 0
    var renderPartialTicks: Float = 0.0f
    private var timerSpeed: Float = 1.0f
    private var elapsedPartialTicks: Float = 0.0f
    private var lastSyncSysClock: Long = 0
    private var lastSyncHRClock: Long = 0
    private var counter: Long = 0
    private var timeSyncAdjustment: Double = 1.0

    init {
        lastSyncSysClock = Minecraft.getSystemTime()
        lastSyncHRClock = System.nanoTime() / 1000000L
    }

    fun updateTimer() {
        val currentTimeMillis = Minecraft.getSystemTime()
        val timeDifferenceMillis = currentTimeMillis - lastSyncSysClock
        val nanoTimeMillis = System.nanoTime() / 1000000L
        val nanoTimeSecs = nanoTimeMillis / 1000.0

        if (timeDifferenceMillis in 0L..1000L) {
            counter += timeDifferenceMillis

            if (counter > 1000L) {
                val hrClockDifference = nanoTimeMillis - lastSyncHRClock
                val timeRatio = counter.toDouble() / hrClockDifference.toDouble()
                timeSyncAdjustment += (timeRatio - timeSyncAdjustment) * 0.20000000298023224
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

        val timeAdjustment = (nanoTimeSecs - lastHRTime) * timeSyncAdjustment
        lastHRTime = nanoTimeSecs

        val clampedAdjustment = timeAdjustment.coerceIn(0.0, 1.0)
        elapsedPartialTicks += (clampedAdjustment * timerSpeed * ticksPerSecond).toFloat()
        elapsedTicks = elapsedPartialTicks.toInt()
        elapsedPartialTicks -= elapsedTicks.toFloat()

        if (elapsedTicks > 10) {
            elapsedTicks = 10
        }

        renderPartialTicks = elapsedPartialTicks
    }
}