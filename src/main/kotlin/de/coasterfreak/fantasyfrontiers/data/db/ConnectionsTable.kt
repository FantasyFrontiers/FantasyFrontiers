package de.coasterfreak.fantasyfrontiers.data.db

import de.coasterfreak.fantasyfrontiers.data.model.town.Connection
import de.coasterfreak.fantasyfrontiers.data.model.town.Town
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a database table for storing connections between towns.
 *
 * This class provides columns for town, connected town, and distance.
 * The primary key consists of the town and connected town columns.
 *
 * @property town The column for the name of the starting town.
 * @property connected_town The column for the name of the connected town.
 * @property distance The column for the distance between the towns.
 */
object ConnectionsTable : Table("connections") {

    val town = varchar("town", 255).references(TownTable.name, onDelete = ReferenceOption.CASCADE)
    val connected_town = varchar("connected_town", 255).references(TownTable.name, onDelete = ReferenceOption.CASCADE)
    val distance = integer("distance").default(0)

    override val primaryKey = PrimaryKey(town, connected_town)
}

/**
 * Returns a list of connections from a given town.
 *
 * @param town The town whose connections need to be retrieved.
 * @return The list of connections for the given town.
 */
fun getAllConnections(townName: String): List<Connection> = transaction {
    ConnectionsTable.select { ConnectionsTable.town eq townName }.map {
        Connection(
            name = it[ConnectionsTable.connected_town],
            distance = it[ConnectionsTable.distance]
        )
    }
}

/**
 * Updates the connection between two towns in the database.
 *
 * @param town The town for which the connection needs to be updated.
 * @param connection The new connection to be added.
 */
fun updateConnection(town: Town, connection: Connection) = transaction {
    ConnectionsTable.replace {
        it[ConnectionsTable.town] = town.name
        it[connected_town] = connection.name
        it[distance] = connection.distance
    }
}