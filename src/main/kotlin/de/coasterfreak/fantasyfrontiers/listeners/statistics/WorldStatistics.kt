package de.coasterfreak.fantasyfrontiers.listeners.statistics

import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class WorldStatistics : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with (event) {
        if(componentId != "ff-world-statistics") return@with
        if (!isFromGuild) return@with
        val languageCode = ServerSettingsCache.get(guild!!.id).language

        event.replyEmbeds(
            EmbedBuilder()
                .setTitle("📜 ${TranslationCache.get(languageCode, "modals.worldStats.title")}")
                .addField(
                    TranslationCache.get(languageCode, "modals.worldStats.fields.guilds").toString(),
                    "```${guild!!.jda.guildCache.size()}```",
                    true
                )
                .build()
        ).setEphemeral(true).queue()
    }
}