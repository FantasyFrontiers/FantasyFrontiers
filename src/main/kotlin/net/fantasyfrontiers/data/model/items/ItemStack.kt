package net.fantasyfrontiers.data.model.items

/**
 * Represents a stack of items.
 *
 * @property item The item in the stack.
 * @property amount The amount of items in the stack. Default value is 1.
 */
data class ItemStack(val item: Item, var amount: Int = 1) {

    init {
        assert(amount > 0 && amount <= item.maxItemStack) { "Amount must be between 1 and ${item.maxItemStack}!" }
    }

    /**
     * Adds the specified amount to the item stack.
     *
     * @param amount The amount to add to the item stack.
     * @return A list of item stacks that represent the result of the addition.
     */
    fun add(amount: Int): List<ItemStack> {
        val itemStacks = mutableListOf<ItemStack>()

        val times = (this.amount + amount) / item.maxItemStack
        val remaining = (this.amount + amount) % item.maxItemStack

        for (i in 0 until times) {
            itemStacks.add(ItemStack(item, item.maxItemStack))
        }
        this.amount = remaining
        itemStacks.add(this)

        return itemStacks
    }

    /**
     * Remove the specified amount from the item stack.
     *
     * @param amount The amount to remove from the item stack.
     * @return `true` if the removal was successful, `false` otherwise.
     */
    fun remove(amount: Int): Boolean {
        if (this.amount < amount) return false

        this.amount -= amount

        if(this.amount == 0) destroy()
        return true
    }

    /**
     * Sets the amount of items in the item stack to 0, effectively destroying the item stack.
     * Todo: Maybe add a destroy event?
     */
    fun destroy() {
        this.amount = 0
    }

    /**
     * Removes the specified amount from the current amount of items.
     * If the resulting amount is negative, the current amount is set to 0.
     * Returns the remaining amount, which is negative if there was not enough amount to remove.
     *
     * @param amount The amount to remove.
     * @return The remaining amount.
     */
    fun removeWithRemainingAmount(amount: Int): Int {
        val remainingAmount = this.amount - amount
        this.amount = if (remainingAmount < 0) 0 else remainingAmount
        return if (remainingAmount < 0) -remainingAmount else 0
    }

}
