package net.fantasyfrontiers.data.db.items

import net.fantasyfrontiers.data.model.items.Inventory
import net.fantasyfrontiers.data.model.items.Item
import net.fantasyfrontiers.utils.DatabaseConnection
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Test class for the InventoriesTableKt functions.
 */
class InventoriesTableKtTest {

    /**
     * Represents an inventory with a specified capacity to store items.
     *
     * @property capacity The maximum number of items that can be stored in the inventory.
     */
    private var inventory = Inventory()
    /**
     * Represents a unique identifier for an object.
     *
     * @property id The identifier value.
     */
    private val id = "TESTVENTORY"

    /**
     * This method is used to perform unit testing on the "testInventory" functionality.
     * It verifies the expected behavior of various inventory operations.
     */
    @Test
    fun testInventoryDatabase() {
        saveInventory(id, inventory)

        assertDoesNotThrow {
            inventory = loadInventory(id)!!
        }
        assertNotNull(inventory)

        inventory.addItem(Item.PEBBLE, 10)
        saveInventory(id, inventory)

        val newInventory = loadInventory(id)
        assertEquals(inventory, newInventory)

        deleteInventory(id)
        assertThrows<NullPointerException> {
            inventory = loadInventory(id)!!
        }
    }

    @Test
    fun testInventoryCapacity() {
        val inventory = Inventory()

        val newInventory = Inventory()
        val itemToTest = Item.DEBUG_ITEM
        assertEquals(0, newInventory.addItem(itemToTest, inventory.capacity * itemToTest.maxItemStack))
        println(newInventory.getFreeSpace())
        assertNotEquals(0, newInventory.addItem(itemToTest))
    }

    /**
     * The `Companion` class contains static methods used for setting up and cleaning up database connections before and after running tests.
     */
    companion object {
        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            DatabaseConnection.connect()
        }

        @JvmStatic
        @AfterAll
        fun cleanup(): Unit {
            DatabaseConnection.disconnect()
        }
    }
}