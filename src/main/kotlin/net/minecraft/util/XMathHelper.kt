package net.minecraft.util

import annotations.Preview
import java.awt.Color
import kotlin.math.*

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
     * @param value The float value.
     * @return The largest integer less than or equal to the value.
     */
    fun floorToInteger(value: Float) = floor(value).toInt()

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
}