package de.coasterfreak.fantasyfrontiers.listeners.game.town

import de.coasterfreak.fantasyfrontiers.data.cache.CharacterCache
import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.data.model.town.Travel
import de.coasterfreak.fantasyfrontiers.manager.TravelManager
import de.coasterfreak.fantasyfrontiers.utils.functions.withTestPermission
import dev.fruxz.ascend.tool.time.calendar.Calendar
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class TravelListener : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        if (event.componentId != "ff-town-menu-travel") return@with
        val character = CharacterCache.get(event.user.id) ?: return@with
        val languageCode = character.language
        val town = character.location

        val embed = EmbedBuilder()
            .setTitle(TranslationCache.get(languageCode, "town.menu.travel").toString())
            .setDescription(
                TranslationCache.get(languageCode, "town.menu.travel.description").toString()
            )
            .setColor(0x57F287)
            .build()

        val selectMenu = StringSelectMenu.create("ff-menu-travel-select")
            .addOptions(
                town.connections.map {
                    SelectOption.of(
                        "${it.name} (${it.distance} km / ${(it.distance/1.6)} miles) - ${it.getTravelDuration(character)}",
                        it.name.lowercase().replace(" ", "_")
                    )
                }
            )
            .build()

        editMessageEmbeds(
            embed
        ).setComponents(
            ActionRow.of(selectMenu)
        ).queue()
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        if (componentId != "ff-menu-travel-select") return@with
        val character = CharacterCache.get(event.user.id) ?: return@with
        val languageCode = character.language
        val town = character.location
        val connection = town.connections.find { it.name.lowercase().replace(" ", "_") == selectedOptions[0].value } ?: return@with
        val serverSettings = ServerSettingsCache.get(event.guild!!.id)

        withTestPermission {
            ((serverSettings.systemAnnouncement.getChannelOrNull(guild) ?: channel) as? TextChannel)?.let {
                it.createThreadChannel(
                    "${town.name} - ${connection.name}",
                    true
                ).queue { thread ->
                    thread.addThreadMember(event.user).queue()

                    val embed = EmbedBuilder()
                        .setTitle(TranslationCache.get(languageCode, "town.menu.travel.started").toString())
                        .setDescription(
                            TranslationCache.get(languageCode, "town.menu.travel.started.description", mapOf(
                                "start" to town.name,
                                "destination" to connection.name,
                                "distance" to "(${connection.distance} km / ${(connection.distance/1.6)} miles)",
                                "eta" to connection.getEta(character),
                                "thread" to thread.asMention
                            )).toString()
                        )
                        .setColor(0x57F287)
                        .build()

                    thread.sendMessageEmbeds(
                        embed
                    ).queue()

                    val travel = Travel(
                        character,
                        connection,
                        thread,
                        Calendar.now()
                    )
                    TravelManager.add(travel)

                    editMessageEmbeds(
                        embed
                    ).setComponents().queue()
                }
            }
            return@withTestPermission
        }
    }
}