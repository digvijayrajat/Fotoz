package com.digvijay.fotoz.utils

internal class CalculateRational(private val numerator: Int, var denominator: Int) {

    fun floatValue(): Float {
        return numerator.toFloat() / denominator.toFloat()
    }

    companion object {

        fun parseRational(input: String): CalculateRational? {
            val parts = input.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            try {
                val numerator = Integer.valueOf(parts[0])
                val denominator = Integer.valueOf(parts[1])
                return CalculateRational(numerator, denominator)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

            return null
        }
    }
}
