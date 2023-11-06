package net.minecraft.util.input

class MouseInputFilter {
    private var currentDelta = 0f
    private var previousDelta = 0f
    private var smoothedDelta = 0f

    fun smooth(inputDelta: Float, smoothingFactor: Float): Float {
        currentDelta += inputDelta

        val delta = ((currentDelta - previousDelta) * smoothingFactor).let { delta ->
            smoothedDelta += (delta - smoothedDelta) * 0.5f

            if ((delta > 0.0f && delta > smoothedDelta) || (delta < 0.0f && delta < smoothedDelta)) smoothedDelta else delta
        }

        previousDelta += delta

        return delta
    }

    fun reset() {
        currentDelta = 0f
        previousDelta = 0f
        smoothedDelta = 0f
    }
}

