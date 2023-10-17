package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.data.cache.CharacterCache
import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.utils.functions.createNewCharacter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

class CharacterSetup : ListenerAdapter() {

    private val componentHandlers = mapOf(
        "ff-start-journey" to this::onStartJourneyButton,
        "ff-char-lang-setup-keep" to this::onKeepLanguageButton
    )

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with (event) {
        if(!componentHandlers.containsKey(componentId)) return@with
        if (!isFromGuild) return@with
        val languageCode = ServerSettingsCache.get(guild!!.id).language
        componentHandlers[componentId]?.invoke(event, languageCode)
    }

    private fun onStartJourneyButton(event: ButtonInteractionEvent, languageCode: String) = with(event) {
        val languageName = TranslationCache.get(languageCode, "translation.name").toString()

        val character = CharacterCache.get(user.id)
        if (character != null) {
            reply("The journey is yet to begin, ${character.firstName} ${character.lastName}!").setEphemeral(true).queue()
            return@with
        }

        replyEmbeds(
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

    private fun onKeepLanguageButton(event: ButtonInteractionEvent, languageCode: String) = with(event) {
        openCharacterCreationModal(languageCode)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        if(componentId != "ff-char-lang-setup-language") return@with
        if (!isFromGuild) return@with
        val languageCode = values.first()

        openCharacterCreationModal(languageCode)
    }

    private fun ComponentInteraction.openCharacterCreationModal(languageCode: String) {
        hook.deleteOriginal().queue()

        replyModal(Modal
            .create("ff-character-creator-${languageCode}",
                TranslationCache.get(languageCode, "modals.charSetup.character.title").toString())
            .addComponents(
                ActionRow.of(
                    TextInput.create("firstname", TranslationCache.get(languageCode, "modals.charSetup.character.firstName").toString(), TextInputStyle.SHORT)
                        .setPlaceholder(TranslationCache.get(languageCode, "modals.charSetup.character.firstName.placeholder").toString())
                        .setMinLength(1)
                        .setMaxLength(32)
                        .build()
                ),
                ActionRow.of(
                    TextInput.create("lastname", TranslationCache.get(languageCode, "modals.charSetup.character.lastName").toString(), TextInputStyle.SHORT)
                        .setPlaceholder(TranslationCache.get(languageCode, "modals.charSetup.character.lastName.placeholder").toString())
                        .setMinLength(1)
                        .setMaxLength(32)
                        .build()
                )
            )
            .build()
        ).queue()
    }

    override fun onModalInteraction(event: ModalInteractionEvent) = with(event) {
        if (!isFromGuild) return@with
        if (!modalId.startsWith("ff-character-creator-")) return@with
        val languageCode = modalId.substringAfter("ff-character-creator-")

        val firstName = values.firstOrNull { it.id == "firstname" }?.asString
        val lastName = values.firstOrNull { it.id == "lastname" }?.asString

        if(firstName == null || lastName == null) {
            reply(TranslationCache.get(languageCode, "modals.charSetup.character.error.noFirstOrLastName").toString()).setEphemeral(true).queue()
            return@with
        }

        // Todo: Create character
        createNewCharacter(user.id, languageCode, firstName, lastName)

        reply(TranslationCache.get(languageCode, "modals.charSetup.character.welcome", mapOf(
            "firstName" to firstName,
            "lastName" to lastName
        )).toString()).setEphemeral(true).queue()
    }

}