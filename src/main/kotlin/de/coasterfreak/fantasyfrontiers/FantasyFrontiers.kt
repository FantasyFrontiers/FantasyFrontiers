package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.db.getAllTowns
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import de.coasterfreak.fantasyfrontiers.manager.RegisterManager.registerAll
import de.coasterfreak.fantasyfrontiers.manager.RegisterManager.registerCommands
import de.coasterfreak.fantasyfrontiers.utils.DatabaseConnection
import de.coasterfreak.fantasyfrontiers.utils.Environment
import kotlin.time.measureTime

class FantasyFrontiers {

    companion object {
        lateinit var instance: FantasyFrontiers
    }

    private val jda: JDA

    init {
        instance = this

        DatabaseConnection.connect()

        TownCache.loadAll()

        jda = JDABuilder.createDefault(Environment.getEnv("BOT_TOKEN"))
            .registerAll()
            .build()
            .awaitReady()
            .registerCommands()

        Runtime.getRuntime().addShutdownHook(Thread {
            jda.shutdown()
            DatabaseConnection.disconnect()
        })

        getItsLogger().info("Bot is ready! ${jda.selfUser.name} - ${jda.selfUser.id} on ${jda.guilds.size} guilds")
    }
}