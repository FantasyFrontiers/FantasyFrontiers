package net.fantasyfrontiers.data.model.items.loot

interface LootConditions<T> {

    fun isLootable(any: T): Boolean

}