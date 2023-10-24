package net.fantasyfrontiers.data.model.items

import com.google.gson.Gson
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 * Represents an inventory with a specified capacity to store items.
 *
 * @property capacity The maximum number of items that can be stored in the inventory.
 */
@Serializable
class Inventory(val capacity: Int = 36) {

    /**
     * Represents a list of item stacks.
     *
     * @property itemStacks The list of item stacks.
     */
    private val itemStacks = mutableListOf<ItemStack>()

    /**
     * Adds an item to the inventory.
     *
     * @param item The item to add.
     * @param amount The amount of the item to add. Default value is 1.
     * @return The amount of items that could not be added to the inventory due to lack of space.
     */
    fun addItem(item: Item, amount: Int = 1): Int {
        var remainingAmount = amount
        var overflown = 0

        while (remainingAmount > 0) {
            val existingStack = itemStacks.firstOrNull { it.item == item && it.amount < item.maxItemStack }

            if (existingStack != null) {
                val canAdd = item.maxItemStack - existingStack.amount
                val toAdd = minOf(canAdd, remainingAmount)

                existingStack.amount += toAdd
                remainingAmount -= toAdd
            } else {
                if (itemStacks.size < capacity) {
                    val toAdd = minOf(item.maxItemStack, remainingAmount)
                    itemStacks.add(ItemStack(item, toAdd))
                    remainingAmount -= toAdd
                } else {
                    overflown += remainingAmount
                    break
                }
            }
        }

        return overflown
    }


    /**
     * Adds an item to the inventory.
     *
     * @param itemStack The item stack to add to the inventory.
     */
    fun addItem(itemStack: ItemStack) = addItem(itemStack.item, itemStack.amount)

    /**
     * Removes the specified amount of an item from the inventory.
     * If the item stack becomes empty, it will be removed from the inventory.
     *
     * @param item The item to remove.
     * @param amount The amount to remove. Default value is 1.
     * @return `true` if the removal was successful, `false` otherwise.
     */
    fun removeItem(item: Item, amount: Int = 1): Boolean {
        val existingStack = itemStacks.firstOrNull { it.item == item }
        if (existingStack != null) {
            existingStack.amount -= amount
            if (existingStack.amount <= 0) {
                itemStacks.remove(existingStack)
            }
            return true
        }
        return false
    }

    /**
     * Removes a specified amount of items from the inventory.
     *
     * @param itemStack The stack of items to be removed.
     * @return `true` if the removal was successful, `false` otherwise.
     */
    fun removeItem(itemStack: ItemStack) = removeItem(itemStack.item, itemStack.amount)

    /**
     * Checks if the inventory has a specific item in sufficient quantity.
     *
     * @param item The item to check.
     * @param amount The desired quantity of the item. Defaults to 1.
     * @return `true` if the inventory has the item in sufficient quantity, `false` otherwise.
     */
    fun hasItem(item: Item, amount: Int = 1): Boolean {
        return getItemAmount(item) >= amount
    }

    /**
     * Retrieves the amount of a specific item in the inventory.
     *
     * @param item The item to retrieve the amount of.
     * @return The amount of the item in the inventory. Returns 0 if the item does not exist in the inventory.
     */
    fun getItemAmount(item: Item): Int {
        var amount = 0
        itemStacks.forEach {
            if (it.item == item) {
                amount += it.amount
            }
        }
        return amount
    }

    /**
     * Retrieves a list of all item stacks in the inventory.
     *
     * @return A list of ItemStack objects.
     */
    fun getItems(): List<ItemStack> {
        return itemStacks.toList()
    }

    /**
     * Retrieves a list of distinct item stacks from the inventory.
     *
     * @return A list of distinct `ItemStack` objects. Each `ItemStack` represents a unique item in the inventory.
     */
    fun getDistinctItems(): List<ItemStack> {
        return itemStacks.groupBy { it.item }.map { (item, itemStacks) ->
            ItemStack(item, itemStacks.sumOf { it.amount }, true)
        }.toList()
    }


    /**
     * Clears the itemStacks list.
     */
    fun clear() {
        itemStacks.clear()
    }

    /**
     * Calculates the amount of free space in the inventory.
     *
     * @return The amount of free space
     */
    fun getFreeSpace(): Int {
        return capacity - itemStacks.size
    }

    /**
     * Retrieves the amount of free space in the inventory.
     *
     * @return The amount of free space in the inventory, which is calculated
     */
    fun getFreeSpace(item: Item): Int {
        val existingStack = itemStacks.firstOrNull { it.item == item }
        if (existingStack != null) {
            return capacity - itemStacks.sumOf { it.amount } + existingStack.amount
        }
        return capacity - itemStacks.sumOf { it.amount }
    }


    fun sellAll(): List<ItemStack> {
        var total = 0L
        val eligibleItems = itemStacks.filter { it.item.isSellable }
        eligibleItems.forEach {
            total += (it.item.worth * it.amount).roundToInt()
        }
        itemStacks.removeAll(eligibleItems)
        modifyCoins(total)
        return total.toCoins()
    }


    fun modifyCoins(amount: Long) {
        val coins = Item.entries.filter { !it.isSellable && it.name.contains("_COIN") }.associateWith { it.worth }
        val coinStacks = itemStacks.filter { coins.containsKey(it.item) }.toMutableList()
        var coinWorths = coinStacks.sumOf { it.amount * coins[it.item]!! } // Worth in bronze coins
        coinWorths += amount
        if (coinWorths < 0) {
            coinWorths = 0.0
        }
        coinStacks.clear()
        coinStacks.addAll(coinWorths.toCoins())
        itemStacks.removeAll { coins.containsKey(it.item) }
        itemStacks.addAll(coinStacks)
    }

    private fun Long.toCoins(): List<ItemStack> {
        return this.toDouble().toCoins()
    }

    private fun Double.toCoins(): List<ItemStack> {
        val coins = Item.entries.filter { !it.isSellable && it.name.contains("_COIN") }.sortedByDescending { it.worth }.associateWith { it.worth }
        val coinStacks = mutableListOf<ItemStack>()
        var remaining = this
        coins.forEach { (coin, worth) ->
            val amount = (remaining / worth).toInt()
            if (amount > 0) {
                coinStacks.add(ItemStack(coin, amount))
                remaining -= amount * worth
            }
        }
        return coinStacks
    }

    override fun equals(other: Any?): Boolean {
        if (other is Inventory) {
            return other.itemStacks == this.itemStacks
        }
        return false
    }

    /**
     * Serializes the object to a JSON string using Gson.
     *
     * @return The serialized JSON string.
     */
    fun serialize(): String {
        return Gson().toJson(this)
    }

    /**
     * Calculates the hash code for the Inventory object.
     *
     * The hash code is calculated based on the capacity and itemStacks fields of the Inventory object. This method utilizes the formula "31 * result + itemStacks.hashCode()" to compute the hash code.
     *
     * @return The hash code for the Inventory object.
     */
    override fun hashCode(): Int {
        var result = capacity
        result = 31 * result + itemStacks.hashCode()
        return result
    }

    /**
     * The `Companion` class contains a single static method for deserializing JSON into an `Inventory` object.
     */
    companion object {
        /**
         * Deserializes a JSON string into an Inventory object.
         *
         * @param json The JSON string to deserialize.
         * @return The deserialized Inventory object.
         */
        fun deserialize(json: String): Inventory {
            return Gson().fromJson(json, Inventory::class.java)
        }
    }
}