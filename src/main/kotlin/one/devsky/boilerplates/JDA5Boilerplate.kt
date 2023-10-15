package one.devsky.boilerplates

import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import one.devsky.boilerplates.manager.RegisterManager.registerAll
import one.devsky.boilerplates.manager.RegisterManager.registerCommands
import one.devsky.boilerplates.utils.Environment

class JDA5Boilerplate {

    companion object {
        lateinit var instance: JDA5Boilerplate
    }

    private val jda: JDA

    init {
        instance = this

        jda = JDABuilder.createDefault(Environment.getEnv("BOT_TOKEN"))
            .registerAll()
            .build()
            .awaitReady()
            .registerCommands()

        getItsLogger().info("Bot is ready! ${jda.selfUser.name} - ${jda.selfUser.id} on ${jda.guilds.size} guilds")
    }
}