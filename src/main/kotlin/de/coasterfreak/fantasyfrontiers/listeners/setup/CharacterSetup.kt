package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class CharacterSetup : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with (event) {
        if(componentId != "ff-start-journey") return@with
        if (!isFromGuild) return@with
        val languageCode = ServerSettingsCache.get(guild!!.id).language

        val languageName = TranslationCache.get(languageCode, "translation.name").toString()
        event.replyEmbeds(
            EmbedBuilder()
                .setTitle("📜 ${TranslationCache.get(languageCode, "modals.charSetup.language.title")}")
                .setDescription(TranslationCache.get(languageCode, "modals.charSetup.language.description").toString())
                .setColor(0x57F287)
                .build()
        )
            .addComponents(
                ActionRow.of(
                    Button.secondary("ff-char-lang-setup-keep", TranslationCache.get(languageCode, "modals.charSetup.language.buttons.keep", mapOf("languageName" to languageName)).toString())
                        .withEmoji(Emoji.fromFormatted("👌🏽")),
                ),
                ActionRow.of(
                    StringSelectMenu.create("ff-char-lang-setup-language")
                        .setPlaceholder(TranslationCache.get(languageCode, "modals.charSetup.language.selectLanguage").toString())
                        .addOptions(
                            TranslationCache.languages.filter { it != languageCode }.map { langCode ->
                                SelectOption.of(
                                    TranslationCache.get(langCode, "translation.name").toString(),
                                    langCode
                                )
                            }
                        )
                        .build()
                )
            )
            .setEphemeral(true).queue()
    }

}