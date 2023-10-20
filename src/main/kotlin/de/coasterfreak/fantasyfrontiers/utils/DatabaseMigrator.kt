package de.coasterfreak.fantasyfrontiers.utils

import com.google.gson.Gson

/**
 * The DatabaseMigrator class is responsible for migrating town data and connections to the database.
 *
 * @property townFile The file containing town data in JSON format.
 * @property gson The Gson instance used for deserializing JSON data.
 */
class DatabaseMigrator {

//    /**
//     * Represents the file containing town data in JSON format.
//     *
//     * @property townFile The file object representing the town data file.
//     */
//    private val townFile = getResourceOrNull("towns.json")
//

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
//        if (townFile == null) {
//            println("towns.json not found!")
//            return
//        }

        DatabaseConnection.connect()
    }

    /**
     * Migrates town data and connections to the database.
     */
    fun migrate() {

//        // ------------ Towns ----------------
//
//        val towns = gson.fromJson(townFile!!.readText(), Array<Town>::class.java)
//
//        getItsLogger().info("Migrating ${towns.size} towns...")
//        val townTime = measureTime {
//            towns.forEach {
//                println("val ${it.name.uppercase().replace(" ", "_")} = $it")
//                println(" ")
//            }
//        }
//        getItsLogger().info("Migrated ${towns.size} towns in ${townTime}. Copy and paste the above code into the Towns object in Towns.kt.")

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