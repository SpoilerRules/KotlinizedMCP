package net.minecraft.util.input

class MouseInputFilter {
    private var currentDelta = 0.0F
    private var previousDelta = 0.0F
    private var smoothedDelta = 0.0F

    fun Float.smooth(smoothingFactor: Float) = also {
        currentDelta += this
        val delta = ((currentDelta - previousDelta) * smoothingFactor).let {
            smoothedDelta += (it - smoothedDelta) * 0.5F
            when {
                it > 0.0F && it > smoothedDelta -> smoothedDelta
                it < 0.0F && it < smoothedDelta -> smoothedDelta
                else -> it
            }
        }
        previousDelta += delta
    }

    fun reset() {
        currentDelta = 0.0F.also { previousDelta = it; smoothedDelta = it }
    }
}