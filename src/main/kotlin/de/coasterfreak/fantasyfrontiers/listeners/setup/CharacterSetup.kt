package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.data.cache.CharacterCache
import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.data.db.player.saveCharacter
import de.coasterfreak.fantasyfrontiers.data.model.player.Skills
import de.coasterfreak.fantasyfrontiers.utils.functions.createNewCharacter
import de.coasterfreak.fantasyfrontiers.utils.functions.sendTranslatedSystemMessage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

class CharacterSetup : ListenerAdapter() {

    /**
     * This variable `buttonHandlers` is a map that associates strings with function references.
     * It is used to handle button interactions in the "CharacterSetup" class.
     *
     * The keys represent the component IDs of the buttons, and the values are functions that handle the button interactions.
     * The button interactions are triggered when a button with a matching component ID is clicked.
     *
     * The `buttonHandlers` map contains the following entries:
     * - Key: "ff-start-journey", Value: Function reference to the `onStartJourneyButton` function in the "CharacterSetup" class.
     * - Key: "ff-char-lang-setup-keep", Value: Function reference to the `onKeepLanguageButton` function in the "CharacterSetup" class.
     *
     * Usage example:
     * The `buttonHandlers` map is used in the `onButtonInteraction` function in the "CharacterSetup" class.
     * This function is triggered when a button is clicked. It checks if the `buttonHandlers` map contains a handler for the clicked button's component ID,
     * retrieves the language code from the server settings, and invokes the corresponding handler function passing the event and language code as parameters.
     */
    private val buttonHandlers = mapOf(
        "ff-start-journey" to this::onStartJourneyButton,
        "ff-char-lang-setup-keep" to this::onKeepLanguageButton
    )

    /**
     * A map of string values to corresponding handler functions for string select interactions.
     *
     * The keys in the map are string values representing the component IDs of the string select menus. The values are
     * reference to the handler functions that should be invoked when a string select interaction with the corresponding
     * component ID occurs.
     *
     * The map is initialized with two entries:
     *
     * - "ff-char-lang-setup-language" : this::onCharLangSetupLanguage
     *     This maps the component ID "ff-char-lang-setup-language" to the function onCharLangSetupLanguage. This handler
     *     function is invoked when a string select interaction with the component ID "ff-char-lang-setup-language" occurs.
     *
     * - "ff-char-starter-skill-select" : this::onStarterSkillSelect
     *     This maps the component ID "ff-char-starter-skill-select" to the function onStarterSkillSelect. This handler
     *     function is invoked when a string select interaction with the component ID "ff-char-starter-skill-select" occurs.
     *
     * @property stringSelectHandlers The map of component IDs to handler functions.
     */
    private val stringSelectHandlers = mapOf(
        "ff-char-lang-setup-language" to this::onCharLangSetupLanguage,
        "ff-char-starter-skill-select" to this::onStarterSkillSelect
    )

    /**
     * Handles button interactions.
     *
     * @param event The ButtonInteractionEvent representing the button interaction.
     */
    override fun onButtonInteraction(event: ButtonInteractionEvent) = with (event) {
        if(!buttonHandlers.containsKey(componentId)) return@with
        if (!isFromGuild) return@with
        val languageCode = ServerSettingsCache.get(guild!!.id).language
        buttonHandlers[componentId]?.invoke(event, languageCode)
    }

    /**
     * Executes the logic for handling a StringSelectInteraction event.
     *
     * @param event The StringSelectInteractionEvent instance representing the interaction event.
     */
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        if(!stringSelectHandlers.containsKey(componentId)) return@with
        if (!isFromGuild) return@with
        stringSelectHandlers[componentId]?.invoke(this)
    }

    /**
     * Handles the event when the "Start Journey" button is clicked.
     *
     * @param event The ButtonInteractionEvent representing the button click event.
     * @param languageCode The language code for localization.
     */
    private fun onStartJourneyButton(event: ButtonInteractionEvent, languageCode: String) = with(event) {
        val languageName = TranslationCache.get(languageCode, "translation.name").toString()

        val character = CharacterCache.get(user.id)
        if (character != null) {
            startJourney()
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

    /**
     * Handles the event when the "Keep Language" button is clicked.
     *
     * @param event The ButtonInteractionEvent triggered by the interaction.
     * @param languageCode The language code selected by the user.
     */
    private fun onKeepLanguageButton(event: ButtonInteractionEvent, languageCode: String) = with(event) {
        openCharacterCreationModal(languageCode)
    }

    /**
     * Handles the setup of the language for the character.
     *
     * This method is triggered when the user selects a language from the language select menu
     * in the character creation modal. It sets the chosen language code and opens the character creation modal.
     *
     * @param event The event object containing information about the interaction.
     */
    private fun onCharLangSetupLanguage(event: StringSelectInteractionEvent) = with(event) {
        val languageCode = values.first()
        openCharacterCreationModal(languageCode)
    }

    /**
     * Opens the character creation modal.
     *
     * @param languageCode The language code of the translation.
     */
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

    /**
     * Handles the interaction with a modal in the CharacterSetup class.
     *
     * @param event The ModalInteractionEvent representing the interaction with the modal.
     */
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

        val character = createNewCharacter(user.id, languageCode, firstName, lastName)

        guild?.sendTranslatedSystemMessage("system.announcement.character.created", mapOf(
            "user" to user.asMention,
            "firstName" to character.firstName,
            "lastName" to character.lastName
        ))

        startJourney()
    }

    /**
     * Allows the user to choose a starter skill.
     *
     * This method presents the user with a selection of starter skills to choose from.
     * The available skills and their descriptions are retrieved from the translation cache
     * using the character's preferred language. The user selects a skill using a select menu.
     * Once a skill is selected, the method handles the selection by invoking the onStarterSkillSelect method.
     *
     * @receiver The reply callback instance.
     */
    private fun IReplyCallback.chooseStarterSkill() {
        val character = CharacterCache.get(user.id)
        if (character == null) {
            errorNoCharacter()
            return
        }

        val languageCode = character.language

        replyEmbeds(
            EmbedBuilder()
                .setTitle(TranslationCache.get(languageCode, "modals.charSetup.starterSkill.title").toString())
                .setDescription(TranslationCache.get(languageCode, "modals.charSetup.starterSkill.description").toString())
                .apply {
                    Skills.STARTER_SKILL_SET.forEach { (skill, level) ->
                        addField(
                            skill.getFormattedName(languageCode, level),
                            skill.getFormattedDescription(languageCode),
                            false
                        )
                    }
                }
                .setColor(0x57F287)
                .build()
        ).addComponents(
            ActionRow.of(
                StringSelectMenu.create("ff-char-starter-skill-select")
                    .setPlaceholder(TranslationCache.get(languageCode, "modals.charSetup.starterSkill.selectSkill").toString())
                    .addOptions(
                        Skills.STARTER_SKILL_SET.map { (skill, level) ->
                            SelectOption.of(
                                skill.getFormattedName(languageCode, level),
                                skill.name
                            )
                        }
                    )
                    .build()
            )
        ).setEphemeral(true).queue()
    }

    /**
     * Handles the logic when a starter skill is selected by the user in the character setup process.
     *
     * This method retrieves the character from the cache using the Discord client ID.
     * If the character is not found, an error message is sent to the user.
     * If the character has already chosen a skill, an error message is sent to the user.
     * Otherwise, the selected skill and level are identified from the starter skill set.
     * The experience corresponding to the level is calculated using the `levelToExperience` function.
     * The character's skills are updated with the selected skill and experience.
     * The updated character is then stored in the cache using the `put` function.
     * The character's journey is started using the `startJourney` function.
     *
     * @param event The StringSelectInteractionEvent representing the user's selection.
     */
    private fun onStarterSkillSelect(event: StringSelectInteractionEvent) = with(event) {
        val character = CharacterCache.get(user.id)
        if (character == null) {
            errorNoCharacter()
            return
        }
        if (character.skills.isNotEmpty()) {
            errorAlreadyChoseSkill()
            return
        }

        val (skill, level) = Skills.STARTER_SKILL_SET.entries.find { it.key.name == values.first() } ?: return@with
        val experience = skill.levelToExperience(level)
        val updated = CharacterCache.put(character.copy(skills = mapOf(skill to experience)))
        saveCharacter(updated)

        startJourney()
        return@with
    }

    /**
     * Starts the journey for the user, assuming they have a character.
     *
     * This method first retrieves the character associated with the user's Discord ID using the `user.id` property.
     * If the character is not found in the cache, the `errorNoCharacter()` method is called and the journey is not started.
     *
     * If the character exists, the language code of the character is obtained.
     *
     * If the character has no skills, the `chooseStarterSkill()` method is called and the journey is not started.
     *
     * If the character has skills, a welcome message is constructed using translations from the `TranslationCache` with placeholders for the character's first name and last name.
     * The first skill of the character is retrieved using the `keys.first()` and `values.first()` properties of the `skills` map.
     * The formatted name of the skill is obtained using the `getFormattedName()` method of the skill, passing the language code and experience as parameters.
     *
     * Finally, the welcome message is sent as a reply using the `reply()` method of the `IReplyCallback` interface.
     * The reply is set to ephemeral and queued for sending.
     */
    private fun IReplyCallback.startJourney() {
        val character = CharacterCache.get(user.id)
        if (character == null) {
            errorNoCharacter()
            return
        }

        val languageCode = character.language

        // Test for Skills
        if (character.skills.isEmpty()) {
            chooseStarterSkill()
            return
        }

        reply(
            "${TranslationCache.get(languageCode, "modals.charSetup.character.welcome", mapOf(
                "firstName" to character.firstName,
                "lastName" to character.lastName
            )).toString()}\n" +
                    "You choose your first skill to be `${character.skills.keys.first().getFormattedName(languageCode, character.skills.values.first())}`.\n" +
                    "Wise choice!"
        ).setEphemeral(true).queue()
    }

    /**
     * Displays an error message when no character is found for the user and sends the error message in an embed as a reply.
     */
    private fun IReplyCallback.errorNoCharacter() {
        val guildLanguage = ServerSettingsCache.get(guild!!.id).language
        replyEmbeds(EmbedBuilder()
            .setDescription("""
                    ${TranslationCache.get(guildLanguage, "modals.errors.charSetup.noCharacter").toString()}
                """.trimIndent())
            .setColor(0xFF3333)
            .build()
        ).setEphemeral(true).queue()
    }

    /**
     * Displays an error message to the user indicating that they have already chosen a skill.
     */
    private fun IReplyCallback.errorAlreadyChoseSkill() {
        val guildLanguage = ServerSettingsCache.get(guild!!.id).language
        replyEmbeds(EmbedBuilder()
            .setDescription("""
                    ${TranslationCache.get(guildLanguage, "modals.errors.charSetup.alreadyChoseSkill").toString()}
                """.trimIndent())
            .setColor(0xFF3333)
            .build()
        ).setEphemeral(true).queue()
    }

}