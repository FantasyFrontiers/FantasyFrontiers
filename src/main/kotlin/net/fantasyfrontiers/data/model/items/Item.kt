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
enum class Item(val worth: Long = 0, val maxItemStack: Int = 99) : TranslatableNameDesc {

    PEBBLE,




    DEBUG_ITEM(0, 1)
    ;


    override fun getTranslationKey(): String {
        return "item.${name}"
    }
}
