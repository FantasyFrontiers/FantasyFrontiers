package net.fantasyfrontiers.utils.extensions

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class MathExtensionsKtTest {

    /**
     * Converts an integer to a Roman numeral representation.
     *
     * @return The Roman numeral representation of the integer.
     */
    @Test
    fun asRomanNumeral() {
        val numbersToTest = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 21, 32, 50, 102, 2034)
        val romanNumeral = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
            "XI", "XV", "XXI", "XXXII", "L", "CII", "MMXXXIV")

        for (i in numbersToTest.indices) {
            assertEquals(romanNumeral[i], numbersToTest[i].asRomanNumeral())
        }
    }

    /**
     * Rounds a double value to the specified number of decimal places.
     *
     * @param decimals The number of decimal places to round to. Default value is 2.
     * @return The rounded double value.
     */
    @Test
    fun round() {
        val numbersToTest = listOf(1.2423, 3.423243, 4.234234, 5.234234, 6.234234, 7.234234, 8.234234, 9.234234, 10.234234, 11.234234, 15.234234, 21.234234, 32.234234, 50.234234, 102.234234, 2034.234234)
        val roundedNumberals = listOf(1.24, 3.42, 4.23, 5.23, 6.23, 7.23, 8.23, 9.23, 10.23, 11.23, 15.23, 21.23, 32.23, 50.23, 102.23, 2034.23)

        for (i in numbersToTest.indices) {
            assertEquals(roundedNumberals[i], numbersToTest[i].round())
        }
    }

    /**
     * Converts a long number to its scientific notation representation.
     *
     * @return The scientific notation representation of the long number.
     */
    @Test
    fun asScientificNumber() {
        val numbersToTest = listOf(1, 99, 999, 1000, 1032, 210423, 230423)
        val scientificNumbers = listOf("1", "99", "999", "1.0k", "1.03k", "210.42k", "230.42k")

        for (i in numbersToTest.indices) {
            assertEquals(scientificNumbers[i], numbersToTest[i].asScientificNumber())
        }
    }
}