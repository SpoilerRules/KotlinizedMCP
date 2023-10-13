package net.minecraft.util;

public class MouseFilter {
    private float currentDelta;
    private float previousDelta;
    private float smoothedDelta;

    public float smooth(float newDelta, float smoothingFactor) {
        currentDelta += newDelta;
        newDelta = (currentDelta - previousDelta) * smoothingFactor;
        smoothedDelta += (newDelta - smoothedDelta) * 0.5F;

        if ((newDelta > 0.0F && newDelta > smoothedDelta) || (newDelta < 0.0F && newDelta < smoothedDelta)) {
            newDelta = smoothedDelta;
        }

        previousDelta += newDelta;
        return newDelta;
    }

    public void reset() {
        currentDelta = 0.0F;
        previousDelta = 0.0F;
        smoothedDelta = 0.0F;
    }
}