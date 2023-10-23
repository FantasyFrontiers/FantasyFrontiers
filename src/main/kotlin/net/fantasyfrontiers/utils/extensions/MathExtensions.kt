package net.fantasyfrontiers.utils.extensions

import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Converts a given long number to a Roman numeral string representation.
 *
 * @return The Roman numeral representation of the long number.
 */
fun Long.asRomanNumeral(): String {
    var number = this
    var result = ""
    while (number > 0) {
        when {
            number >= 1000 -> {
                result += "M"
                number -= 1000
            }
            number >= 900 -> {
                result += "CM"
                number -= 900
            }
            number >= 500 -> {
                result += "D"
                number -= 500
            }
            number >= 400 -> {
                result += "CD"
                number -= 400
            }
            number >= 100 -> {
                result += "C"
                number -= 100
            }
            number >= 90 -> {
                result += "XC"
                number -= 90
            }
            number >= 50 -> {
                result += "L"
                number -= 50
            }
            number >= 40 -> {
                result += "XL"
                number -= 40
            }
            number >= 10 -> {
                result += "X"
                number -= 10
            }
            number >= 9 -> {
                result += "IX"
                number -= 9
            }
            number >= 5 -> {
                result += "V"
                number -= 5
            }
            number >= 4 -> {
                result += "IV"
                number -= 4
            }
            else -> {
                result += "I"
                number -= 1
            }
        }
    }
    return result
}

/**
 * Converts an integer to a Roman numeral representation.
 *
 * @return The Roman numeral representation of the integer.
 */
fun Int.asRomanNumeral(): String = this.toLong().asRomanNumeral()

/**
 * Rounds a double value to the specified number of decimal places.
 *
 * @param decimals The number of decimal places to round to. Default value is 2.
 * @return The rounded double value.
 */
fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

/**
 * Converts a long number to its scientific notation representation.
 *
 * @return The scientific notation representation of the long number.
 */
fun Long.asScientificNumber(): String {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = toDouble()
    val value = floor(log10(numValue)).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        (numValue / 10.0.pow((base * 3).toDouble())).round(2).toString() + suffix[base]
    } else {
        toString()
    }
}

fun Int.asScientificNumber(): String = this.toLong().asScientificNumber()