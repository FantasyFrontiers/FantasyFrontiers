package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.annotations.SlashCommand
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.data.db.discord.loadServerSettings
import de.coasterfreak.fantasyfrontiers.data.db.discord.updateServerSettings
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommands
import de.coasterfreak.fantasyfrontiers.utils.functions.withTestPermission
import dev.fruxz.ascend.extension.getResourceOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.utils.FileUpload

@SlashCommand("setup", "Setup commands for the game.")
class SetupCommand : ListenerAdapter(), HasSubcommands {
    override fun getSubCommands(): List<SubcommandData> = listOf(
        SubcommandData("panels", "Setup the panels for the game.")
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) = with(event) {
        if(name != "setup") return@with
        when(subcommandName) {
            "panels" -> {
                replyEmbeds(
                    EmbedBuilder()
                    .setTitle("Welcome to Fantasy Frontiers!")
                    .setDescription("Please select your language below")
                    .setColor(0x00AA00)
                    .build()
                ).addComponents(
                    ActionRow.of(
                        StringSelectMenu.create("ff-setup-language")
                            .addOptions(
                                TranslationCache.languages.map { languageCode ->
                                    SelectOption.of(
                                        TranslationCache.get(languageCode, "translation.name").toString(),
                                        languageCode
                                    )
                                }
                            )
                            .build()
                    )
                ).setEphemeral(true).queue()
            }
            else -> {
                reply("Unknown subcommand.").queue()
            }
        }
    }


    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        if (componentId != "ff-setup-language") return@with
        if (!isFromGuild) return@with
        val languageCode = values.first()

        val serverSettings = loadServerSettings(guild!!.id)
        updateServerSettings(serverSettings.copy(language = languageCode))

        editMessage("Setting up language...").setEmbeds().queue()
        withTestPermission {
            channel.asTextChannel().sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle(TranslationCache.get(languageCode, "modals.startPanel.title").toString())
                    .setDescription(TranslationCache.get(languageCode, "modals.startPanel.description").toString())
                    .setColor(0x00AA00)
                    .build()
            ).addFiles(
                getResourceOrNull("fantasy-frontiers.png")?.let {
                    FileUpload.fromData(
                        it
                    )
                }
            ).addComponents(
                ActionRow.of(
                    Button.success("ff-start-journey", TranslationCache.get(languageCode, "modals.startPanel.button").toString())
                        .withEmoji(Emoji.fromFormatted("🎯")),
                    Button.secondary("ff-world-statistics", TranslationCache.get(languageCode, "modals.worldStats.title").toString())
                        .withEmoji(Emoji.fromFormatted("📜"))
                )
            ).queue()

            deleteOriginal().queue()
        }
    }
}