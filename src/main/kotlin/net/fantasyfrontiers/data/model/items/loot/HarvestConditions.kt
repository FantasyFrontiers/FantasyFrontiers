package net.fantasyfrontiers.data.model.items.loot

import net.fantasyfrontiers.data.model.town.Town

data class HarvestConditions (
    val minTemperature: Int = 10,
    val maxTemperature: Int = 30,
    val minHumidity: Int = 30,
    val maxHumidity: Int = 60,
) : LootConditions<Town> {

    override fun isLootable(any: Town): Boolean {
        return any.temperature in minTemperature..maxTemperature && any.humidity in minHumidity..maxHumidity
    }

}
