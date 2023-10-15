package de.coasterfreak.fantasyfrontiers.data.db

import de.coasterfreak.fantasyfrontiers.data.model.Coords
import de.coasterfreak.fantasyfrontiers.data.model.Features
import de.coasterfreak.fantasyfrontiers.data.model.Town
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a database table for storing town information.
 *
 * @property name The column for storing the name of the town.
 * @property population The column for storing the population of the town.
 * @property type The column for storing the type of the town.
 * @property x The column for storing the x-coordinate of the town.
 * @property y The column for storing the y-coordinate of the town.
 * @property capital The column for storing whether the town is a capital.
 * @property citadel The column for storing whether the town has a citadel.
 * @property plaza The column for storing whether the town has a plaza.
 * @property port The column for storing whether the town has a port.
 * @property shanty The column for storing whether the town has a shanty.
 * @property temple The column for storing whether the town has a temple.
 */
object TownTable : Table("towns") {

    val name = varchar("name", 255)
    val population = long("population").default(0)
    val type = varchar("type", 255).default("Generic")
    val x = double("x").default(0.0)
    val y = double("y").default(0.0)

    // Features
    val capital = bool("capital").default(false)
    val citadel = bool("citadel").default(false)
    val plaza = bool("plaza").default(false)
    val port = bool("port").default(false)
    val shanty = bool("shanty").default(false)
    val temple = bool("temple").default(false)

    override val primaryKey = PrimaryKey(name)
}

/**
 * Retrieves all towns from the database and returns them as a list of Town objects.
 *
 * @return A list of all towns.
 */
fun getAllTowns() = transaction {
    TownTable.selectAll().map {
        Town(
            name = it[TownTable.name],
            population = it[TownTable.population],
            type = it[TownTable.type],
            coords = Coords(
                x = it[TownTable.x],
                y = it[TownTable.y]
            ),
            features = Features(
                capital = it[TownTable.capital],
                citadel = it[TownTable.citadel],
                plaza = it[TownTable.plaza],
                port = it[TownTable.port],
                shanty = it[TownTable.shanty],
                temple = it[TownTable.temple]
            ),
            connections = getAllConnections(it[TownTable.name])
        )
    }
}

/**
 * Updates the data of a town in the database.
 *
 * @param town The town object with the updated data.
 */
fun updateTown(town: Town) = transaction {
    TownTable.replace {
        it[name] = town.name
        it[population] = town.population
        it[type] = town.type
        it[x] = town.coords.x
        it[y] = town.coords.y
        it[capital] = town.features.capital
        it[citadel] = town.features.citadel
        it[plaza] = town.features.plaza
        it[port] = town.features.port
        it[shanty] = town.features.shanty
        it[temple] = town.features.temple
    }
}