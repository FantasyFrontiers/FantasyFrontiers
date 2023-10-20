package net.fantasyfrontiers.data.model.items

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for the `ItemStack` class.
 */
class ItemStackTest {

    /**
     * Adds the specified amount to the item stack.
     *
     * @param amount The amount to add to the item stack.
     * @return A list of item stacks that represent the result of the addition.
     */
    @Test
    fun create() {
        val itemStack = ItemStack(Item("testItem"))
        assertEquals(1, itemStack.amount)

        val itemStack2 = ItemStack(Item("testItem"), 99)
        assertEquals(99, itemStack2.amount)

        assertThrows<AssertionError> { ItemStack(Item("testItem"), 100) }
        assertThrows<AssertionError> { ItemStack(Item("testItem"), 0) }

        val itemStack3 = ItemStack(Item("testItem", 1), 1)
        assertEquals(1, itemStack3.amount)
    }

    /**
     * Adds the specified amount to the item stack.
     *
     * @param amount The amount to add to the item stack.
     * @return A list of item stacks that represent the result of the addition.
     */
    @Test
    fun add() {
        val itemStack = ItemStack(Item("testItem"), 1)

        val result = itemStack.add(1)
        assertEquals(1, result.size)
        assertEquals(2, result[0].amount)
        assertEquals(2, itemStack.amount)

        val result2 = itemStack.add(98)
        assertEquals(2, result2.size)
        assertEquals(99, result2[0].amount)
        assertEquals(1, result2[1].amount)
    }

    /**
     * Remove the specified amount from the item stack.
     *
     * @param amount The amount to remove from the item stack.
     * @return `true` if the removal was successful, `false` otherwise.
     */
    @Test
    fun remove() {
        val itemStack = ItemStack(Item("testItem"), 10)

        assertTrue(itemStack.remove(5))
        assertEquals(5, itemStack.amount)

        assertFalse(itemStack.remove(6))
        assertEquals(5, itemStack.amount)

        assertTrue(itemStack.remove(5))
        assertEquals(0, itemStack.amount)
    }

    /**
     * Removes the specified amount from the current amount of items in the item stack.
     * If the resulting amount is negative, the current amount is set to 0.
     * Returns the remaining amount, which is negative if there was not enough amount to remove.
     *
     * @param amount The amount to remove.
     * @return The remaining amount.
     */
    @Test
    fun removeWithRemainingAmount() {
        val itemStack = ItemStack(Item("testItem"), 10)

        assertTrue(itemStack.remove(5))
        assertEquals(5, itemStack.amount)

        assertEquals(1, itemStack.removeWithRemainingAmount(6))
    }
}