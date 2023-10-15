package de.coasterfreak.fantasyfrontiers.utils

import com.google.gson.Gson
import de.coasterfreak.fantasyfrontiers.data.db.getAllTowns
import de.coasterfreak.fantasyfrontiers.data.db.updateConnection
import de.coasterfreak.fantasyfrontiers.data.db.updateTown
import de.coasterfreak.fantasyfrontiers.data.model.Town
import dev.fruxz.ascend.extension.getResourceOrNull
import dev.fruxz.ascend.extension.logging.getItsLogger
import kotlin.io.path.readText
import kotlin.time.measureTime

class DatabaseMigrator {

    private val townFile = getResourceOrNull("towns.json")
    private val gson = Gson()

    init {
        preMigrate()
    }

    private fun preMigrate() {
        if (townFile == null) {
            println("towns.json not found!")
            return
        }

        DatabaseConnection.connect()
    }

    fun migrate() {
        val towns = gson.fromJson(townFile!!.readText(), Array<Town>::class.java)

        getItsLogger().info("Migrating ${towns.size} towns...")
        val townTime = measureTime {
            towns.forEach {
                getItsLogger().info("Migrating town ${it.name}...")
                updateTown(it)
            }
        }
        getItsLogger().info("Migrated ${towns.size} towns in ${townTime}.")

        getItsLogger().info("Migrating connections...")
        var connections = 0
        val connectionsTime = measureTime {
            towns.forEach { town ->
                town.connections.forEach { connection ->
                    updateConnection(town, connection)
                    getItsLogger().info("Migrated connection ${town.name} -> ${connection.name}")
                    connections++
                }
            }
        }
        getItsLogger().info("Migrated $connections connections in ${connectionsTime}.")

        postMigrate()
    }

    private fun postMigrate() {
        DatabaseConnection.disconnect()
    }
}