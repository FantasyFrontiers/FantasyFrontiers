package de.coasterfreak.fantasyfrontiers.utils

import com.google.gson.Gson
import de.coasterfreak.fantasyfrontiers.data.db.updateConnection
import de.coasterfreak.fantasyfrontiers.data.db.updateTown
import de.coasterfreak.fantasyfrontiers.data.model.town.Town
import dev.fruxz.ascend.extension.getResourceOrNull
import dev.fruxz.ascend.extension.logging.getItsLogger
import kotlin.io.path.readText
import kotlin.time.measureTime

/**
 * The DatabaseMigrator class is responsible for migrating town data and connections to the database.
 *
 * @property townFile The file containing town data in JSON format.
 * @property gson The Gson instance used for deserializing JSON data.
 */
class DatabaseMigrator {

    /**
     * Represents the file containing town data in JSON format.
     *
     * @property townFile The file object representing the town data file.
     */
    private val townFile = getResourceOrNull("towns.json")


    /**
     * The Gson instance used for JSON serialization and deserialization.
     */
    private val gson = Gson()

    init {
        preMigrate()
    }

    /**
     * Performs pre-migration operations before migrating town data and connections to the database.
     */
    private fun preMigrate() {
        if (townFile == null) {
            println("towns.json not found!")
            return
        }

        DatabaseConnection.connect()
    }

    /**
     * Migrates town data and connections to the database.
     */
    fun migrate() {

        // ------------ Towns ----------------

        val towns = gson.fromJson(townFile!!.readText(), Array<Town>::class.java)

        getItsLogger().info("Migrating ${towns.size} towns...")
        val townTime = measureTime {
            towns.forEach {
                updateTown(it)
            }
        }
        getItsLogger().info("Migrated ${towns.size} towns in ${townTime}.")

        getItsLogger().info("Migrating connections...")
        var connections = 0
        val connectionTime = measureTime {
            towns.forEach { town ->
                town.connections.forEach { connection ->
                    updateConnection(town, connection)
                    connections++
                }
            }
        }
        getItsLogger().info("Migrated $connections connections in ${connectionTime}.")


        // ------------ Players ----------------



        postMigrate()
    }

    /**
     * Performs post-migration tasks after migrating town data and connections to the database.
     * This method disconnects from the database by closing the connection.
     */
    private fun postMigrate() {
        DatabaseConnection.disconnect()
    }
}