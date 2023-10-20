package net.fantasyfrontiers.utils.extensions

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

fun Int.asRomanNumeral(): String = this.toLong().asRomanNumeral()

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()