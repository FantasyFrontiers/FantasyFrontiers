package net.fantasyfrontiers.data.model.items

import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.model.extras.TranslatableNameDesc
import net.fantasyfrontiers.data.model.items.loot.HarvestConditions
import net.fantasyfrontiers.data.model.town.Town

/**
 * Represents an item in the inventory with its name, worth, and maximum stack size.
 *
 * @property name The name of the item.
 * @property worth The worth of the item in currency.
 * @property maxItemStack The maximum number of items that can be stacked together.
 */
@Serializable
enum class Item(val worth: Double = 0.1, val maxItemStack: Int = 99, private val harvestConditions: HarvestConditions? = null, val isSellable: Boolean = true) : TranslatableNameDesc {

    PEBBLE,

    // Harvestable Herbs and Vegetables
    APPLE(2.0, 99, HarvestConditions(10, 30, 30, 60)),
    PEAR(3.0, 99, HarvestConditions(10, 25, 35, 65)),
    PLUM(3.0, 99, HarvestConditions(12, 28, 30, 70)),
    CHERRY(4.0, 99, HarvestConditions(12, 27, 35, 65)),
    BLACKBERRY(5.0, 99, HarvestConditions(10, 25, 40, 70)),
    STRAWBERRY(4.0, 99, HarvestConditions(10, 25, 35, 70)),
    GRAPE(3.0, 99, HarvestConditions(15, 30, 20, 60)),

    // Herbs
    MINT(6.0, 99, HarvestConditions(15, 30, 40, 80)),
    ROSEMARY(7.0, 99, HarvestConditions(10, 30, 20, 50)),
    THYME(6.0, 99, HarvestConditions(10, 30, 25, 55)),
    BASIL(6.0, 99, HarvestConditions(18, 30, 40, 70)),
    SAGE(8.0, 99, HarvestConditions(15, 30, 30, 60)),
    OREGANO(7.0, 99, HarvestConditions(10, 28, 30, 70)),
    LAVENDER(9.0, 99, HarvestConditions(15, 30, 25, 55)),


    // Money System
    BRONZE_COIN(1.0, 99, isSellable = false),
    SILVER_COIN(1000.0, 999, isSellable = false),
    GOLD_COIN(1000000.0, 999, isSellable = false),
    PLATINUM_COIN(1000000000.0, Int.MAX_VALUE, isSellable = false),

    DEBUG_ITEM(0.0, 1, isSellable = false)
    ;


    override fun getTranslationKey(): String {
        return "item.${name}"
    }

    fun canBeHarvested(town: Town): Boolean {
        return harvestConditions?.isLootable(town) ?: false
    }

    companion object {
        @JvmStatic
        fun getHarvestables(town: Town): List<Item> {
            return entries.filter { it.canBeHarvested(town) }
        }
    }
}
