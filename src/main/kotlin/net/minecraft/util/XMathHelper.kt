package net.minecraft.util

import annotations.Preview
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

@Preview
object XMathHelper {

    /**
     * Calculates the sine of a float value.
     * @param value The float value.
     * @return The sine of the value.
     */
    fun sin(value: Float): Float = kotlin.math.sin(value)

    /**
     * Calculates the cosine of a float value.
     * @param value The float value.
     * @return The cosine of the value.
     */
    fun cos(value: Float): Float = kotlin.math.cos(value)

    /**
     * Calculates the square root of a float value.
     * @param value The float value.
     * @return The square root of the value.
     */
    fun squareRootFloat(value: Float): Float = sqrt(value)

    /**
     * Calculates the square root of a double value.
     * @param value The double value.
     * @return The square root of the value.
     */
    fun squareRootDouble(value: Double): Double = sqrt(value)

    /**
     * Floors a float value to an integer.
     *
     * @param value The float value.
     * @return The largest integer less than or equal to the value.
     * @throws Error if the value is not a float or double.
     */
    fun floorToInteger(value: Any): Int = when (value) {
        is Float -> floor(value).toInt()
        is Double -> floor(value).toInt()
        else -> throw Error("Value must be float or double")
    }

    /**
     * Calculates the arc tangent of y/x in radians, with a range from -PI to PI.
     * @param y The ordinate coordinate.
     * @param x The abscissa coordinate.
     * @return The arc tangent of y/x, in radians.
     */
    fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)

    /**
     * Converts HSV color values to an RGB integer representation.
     * @param h The hue component of the color.
     * @param s The saturation component of the color.
     * @param v The brightness component of the color.
     * @return An integer representation of the RGB color values.
     */
    fun convertHSVToRGB(h: Float, s: Float, v: Float) = Color.getHSBColor(h, s, v).rgb

    /**
     * Rounds up a number to the nearest multiple.
     * @param num The number to be rounded up.
     * @param multiple The multiple to round up to.
     * @return The rounded-up result.
     */
    fun roundUpToNearestMultiple(num: Int, multiple: Int): Int =
        (multiple * ceil(num.toDouble() / multiple.toDouble())).toInt()

    /**
     * Performs linear interpolation (lerp) between two values.
     *
     * Linear interpolation calculates a value between two endpoints based on a factor.
     * The interpolation factor should be in the range [0, 1], where 0 corresponds to the start value,
     * 1 corresponds to the end value, and values in between represent a proportional blend between the two.
     *
     * @param interpolationFactor The factor determining the interpolation amount.
     *                           Should be in the range [0, 1].
     * @param start The starting value.
     * @param end The ending value.
     * @return The result of linear interpolation between the start and end values.
     */
    fun lerp(interpolationFactor: Double, start: Double, end: Double): Double =
        start + interpolationFactor * (end - start)
}