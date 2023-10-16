package de.coasterfreak.fantasyfrontiers.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.coasterfreak.fantasyfrontiers.data.db.ConnectionsTable
import de.coasterfreak.fantasyfrontiers.data.db.TownTable
import de.coasterfreak.fantasyfrontiers.data.db.discord.ServerSettingsTable
import dev.fruxz.ascend.extension.logging.getItsLogger
import dev.fruxz.ascend.tool.time.calendar.Calendar
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * A singleton object representing a connection to the database.
 */
internal object DatabaseConnection {

    /**
     * Configuration class for the database connection.
     */
    private val dbConfig = HikariConfig().apply {
        jdbcUrl = Environment.getEnv("DATABASE_URL")
        driverClassName =Environment.getEnv("DATABASE_DRIVER")
        username = Environment.getEnv("DATABASE_USER")
        password = Environment.getEnv("DATABASE_PASSWORD")
        maximumPoolSize = 100
    }
    /**
     * Represents a database connection.
     *
     * This variable is used to establish a connection to the database using the provided database configuration.
     * It is a private property, so it can only be accessed within the scope of its containing class.
     *
     * @property database The database connection instance.
     */
    private val database = Database.connect(HikariDataSource(dbConfig))

    /**
     * Connects to the database and performs necessary operations.
     */
    fun connect() {
        getItsLogger().info("Connecting to database...")
        database

        getItsLogger().info("Check for table updates...")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                TownTable,
                ConnectionsTable,
                ServerSettingsTable
            )
        }

        getItsLogger().info("Connected to database.")
    }

    /**
     * Disconnects from the database.
     * This method closes the database connection and logs a message indicating disconnection.
     */
    fun disconnect() {
        database.connector().close()
        getItsLogger().info("Disconnected from database.")
    }
}

/**
 * Converts an [Instant] to a [Calendar].
 *
 * This method takes an [Instant] and converts it to a [Calendar] object. The [Instant] represents a point in time,
 * while the [Calendar] represents a date and time in a specific calendar system.
 *
 * @return The [Calendar] representation of the [Instant].
 */
fun Instant.toCalendar() =
    Calendar(GregorianCalendar.from(ZonedDateTime.from(this.atZone(ZoneId.systemDefault()))))