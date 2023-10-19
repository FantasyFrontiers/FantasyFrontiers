package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.annotations.SlashCommand
import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
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
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.utils.FileUpload

@SlashCommand("setup", "Setup commands for the game.", true)
class SetupCommand : ListenerAdapter(), HasSubcommands {
    override fun getSubCommands(): List<SubcommandData> = listOf(
        SubcommandData("panels", "Setup the panels for the game."),
        SubcommandData("advanced", "Setup the advanced settings for the game.")
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) = with(event) {
        if(name != "setup") return@with
        if (!isFromGuild) {
            reply("This command can only be used in a server.").queue()
            return@with
        }
        when(subcommandName) {
            "panels" -> {
                replyEmbeds(
                    EmbedBuilder()
                    .setTitle("Welcome to Fantasy Frontiers!")
                    .setDescription("Please select your language below to start the setup process. \n\n" +
                        "If you want to change your language later,\n" +
                            "you can do so by using the `/setup panels` command again." +
                            "\n\n\n\n**Want to help translate the bot?**\n" +
                            "You can help translate the bot by looking at our [Weblate](https://weblate.flawcra.cc/projects/fantasy-frontiers/translations/)")
                    .setColor(0x57F287)
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
            "advanced" -> advancedSetup()
            else -> {
                reply("Unknown subcommand.").queue()
            }
        }
    }


    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        if (componentId != "ff-setup-language") return@with
        if (!isFromGuild) return@with
        val languageCode = values.first()

        val serverSettings = ServerSettingsCache.get(guild!!.id).copy(language = languageCode)
        updateServerSettings(serverSettings)
        ServerSettingsCache.put(serverSettings)

        editMessage("Setting up language...").setEmbeds().queue()
        withTestPermission {
            this@with.channel.asTextChannel().sendMessageEmbeds(
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
                    Button.success("ff-start-journey", TranslationCache.get(languageCode, "modals.startPanel.start-button").toString())
                        .withEmoji(Emoji.fromFormatted("🎯")),
                    Button.secondary("ff-world-statistics", TranslationCache.get(languageCode, "modals.worldStats.title").toString())
                        .withEmoji(Emoji.fromFormatted("📜"))
                )
            ).queue()

            advancedSetup()
        }
    }

    private fun IReplyCallback.advancedSetup() {
        val serverSettings = ServerSettingsCache.get(guild!!.id)
        replyEmbeds(
            EmbedBuilder()
                .setTitle(TranslationCache.get(serverSettings.language, "modals.advancedSetup.title").toString())
                .setDescription(TranslationCache.get(serverSettings.language, "modals.advancedSetup.description").toString())
                .setColor(0x57F287)
                .build()
        ).addComponents(
            ActionRow.of(
                Button.secondary("ff-setup-advanced-system-announcement", TranslationCache.get(serverSettings.language, "modals.advancedSetup.buttons.system-announcement").toString())
                    .withEmoji(Emoji.fromFormatted("📑")),
                Button.secondary("ff-setup-advanced-roles", TranslationCache.get(serverSettings.language, "modals.advancedSetup.roles.title").toString())
                    .withEmoji(Emoji.fromFormatted("🫅🏽")),
            )
        ).setEphemeral(true).queue()
    }
}