package de.coasterfreak.fantasyfrontiers.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.coasterfreak.fantasyfrontiers.data.db.ConnectionsTable
import de.coasterfreak.fantasyfrontiers.data.db.TownTable
import dev.fruxz.ascend.extension.logging.getItsLogger
import dev.fruxz.ascend.tool.time.calendar.Calendar
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

internal object DatabaseConnection {

    private val dbConfig = HikariConfig().apply {
        jdbcUrl = Environment.getEnv("DATABASE_URL")
        driverClassName =Environment.getEnv("DATABASE_DRIVER")
        username = Environment.getEnv("DATABASE_USER")
        password = Environment.getEnv("DATABASE_PASSWORD")
        maximumPoolSize = 100
    }
    private val database = Database.connect(HikariDataSource(dbConfig))

    fun connect() {
        getItsLogger().info("Connecting to database...")
        database

        getItsLogger().info("Check for table updates...")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                TownTable,
                ConnectionsTable
            )
        }

        getItsLogger().info("Connected to database.")
    }

    fun disconnect() {
        database.connector().close()
        getItsLogger().info("Disconnected from database.")
    }
}

fun Instant.toCalendar() =
    Calendar(GregorianCalendar.from(ZonedDateTime.from(this.atZone(ZoneId.systemDefault()))))