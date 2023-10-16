package de.coasterfreak.fantasyfrontiers

import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.manager.RegisterManager.registerAll
import de.coasterfreak.fantasyfrontiers.manager.RegisterManager.registerCommands
import de.coasterfreak.fantasyfrontiers.utils.DatabaseConnection
import de.coasterfreak.fantasyfrontiers.utils.Environment
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

/**
 * The FantasyFrontiers class represents the main entry point of the application.
 * It initializes the necessary components, connects to the database, loads all towns into cache,
 * registers listeners, event handlers, and commands, and starts the bot.
 */
class FantasyFrontiers {

    companion object {
        lateinit var instance: FantasyFrontiers
    }

    private val jda: JDA

    init {
        instance = this

        DatabaseConnection.connect()

        TranslationCache.loadAll()
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