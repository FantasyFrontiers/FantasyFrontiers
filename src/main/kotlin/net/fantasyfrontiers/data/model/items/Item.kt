package net.fantasyfrontiers.data.model.items

import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.model.extras.TranslatableNameDesc

/**
 * Represents an item in the inventory with its name, worth, and maximum stack size.
 *
 * @property name The name of the item.
 * @property worth The worth of the item in currency.
 * @property maxItemStack The maximum number of items that can be stacked together.
 */
@Serializable
enum class Item(val worth: Double = 0.01, val maxItemStack: Int = 99) : TranslatableNameDesc {

    PEBBLE,



    // Money System
    BRONZE_COIN(1.0, 99),
    SILVER_COIN(1000.0, 999),
    GOLD_COIN(1000000.0, 999),
    PLATINUM_COIN(1000000000.0, Int.MAX_VALUE),

    DEBUG_ITEM(0.0, 1)
    ;


    override fun getTranslationKey(): String {
        return "item.${name}"
    }
}
