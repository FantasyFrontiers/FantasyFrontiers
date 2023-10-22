package net.fantasyfrontiers.data.model.items

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class InventoryTest {

    @Test
    fun getDistinctItems() {
        val inventory = Inventory()
        assertEquals(0, inventory.getDistinctItems().size)

        inventory.addItem(Item.PEBBLE, Item.PEBBLE.maxItemStack)
        assertEquals(1, inventory.getDistinctItems().size)

        inventory.addItem(Item.PEBBLE, 32)
        assertEquals(1, inventory.getDistinctItems().size)
        assertEquals(Item.PEBBLE.maxItemStack + 32, inventory.getDistinctItems().first().amount)
    }


    @Test
    fun getItemAmount() {
        val inventory = Inventory()
        assertEquals(0, inventory.getItemAmount(Item.PEBBLE))

        inventory.addItem(Item.PEBBLE, Item.PEBBLE.maxItemStack)
        assertEquals(Item.PEBBLE.maxItemStack, inventory.getItemAmount(Item.PEBBLE))

        inventory.addItem(Item.PEBBLE, 32)
        assertEquals(Item.PEBBLE.maxItemStack + 32, inventory.getItemAmount(Item.PEBBLE))
    }
}