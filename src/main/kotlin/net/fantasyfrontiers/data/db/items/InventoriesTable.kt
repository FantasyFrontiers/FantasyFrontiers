package net.fantasyfrontiers.data.db.items

import net.fantasyfrontiers.data.model.items.Inventory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a table in the database to store inventories.
 *
 * @property id The column representing the inventory ID.
 * @property inventory The column representing the inventory data.
 */
object InventoriesTable : Table("inventories") {

    val id = varchar("id", 36)
    val inventory = blob("inventory")

    override val primaryKey = PrimaryKey(id)
}

/**
 * Loads an inventory with the specified ID from the database.
 *
 * @param id The ID of the inventory to load.
 * @return The loaded Inventory object, or null if the inventory does not exist.
 */
fun loadInventory(id: String) = transaction {
    InventoriesTable.select { InventoriesTable.id eq id }.firstOrNull()?.let { resultRow ->
        val inventoryString = resultRow[InventoriesTable.inventory].bytes.inputStream().bufferedReader().use { it.readText() }
        Inventory.deserialize(inventoryString)
    }
}

/**
 * Saves the inventory with the specified ID to the database.
 *
 * @param id The identifier of the inventory.
 * @param inventory The inventory object to be saved.
 */
fun saveInventory(id: String, inventory: Inventory) = transaction {
    InventoriesTable.replace {
        it[InventoriesTable.id] = id
        it[InventoriesTable.inventory] = ExposedBlob(inventory.serialize().toByteArray())
    } get InventoriesTable.id
}


/**
 * Deletes an inventory from the database based on the specified ID.
 *
 * @param id The ID of the inventory to be deleted.
 */
fun deleteInventory(id: String) = transaction {
    InventoriesTable.deleteWhere { InventoriesTable.id eq id }
}
