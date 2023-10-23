package net.fantasyfrontiers

import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.manager.RegisterManager.registerAll
import net.fantasyfrontiers.manager.RegisterManager.registerCommands
import net.fantasyfrontiers.manager.TravelManager
import net.fantasyfrontiers.utils.DatabaseConnection
import net.fantasyfrontiers.utils.Environment
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.fantasyfrontiers.utils.extensions.asScientificNumber

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

        jda = JDABuilder.createDefault(Environment.getEnv("BOT_TOKEN"))
            .registerAll()
            .build()
            .awaitReady()
            .registerCommands()

        CharacterCache.loadStatistics()
        TravelManager.startThread()

        Runtime.getRuntime().addShutdownHook(Thread {
            getItsLogger().info("Shutting down...")
            TravelManager.stopThread()
            jda.shutdown()
            DatabaseConnection.disconnect()
        }.apply { isDaemon = true })

        getItsLogger().info("Bot is ready! ${jda.selfUser.name} - ${jda.selfUser.id} on ${jda.guilds.size} guilds")
    }

    fun refreshStatus() {
        jda.presence.activity = Activity.customStatus("🌸 ${CharacterCache.totalCharacters.asScientificNumber()} characters created")
    }
}