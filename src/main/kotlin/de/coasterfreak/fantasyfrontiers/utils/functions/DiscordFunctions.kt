package de.coasterfreak.fantasyfrontiers.utils.functions

import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.manager.TravelManager
import dev.fruxz.ascend.extension.logging.getItsLogger
import dev.fruxz.ascend.extension.tryOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

/**
 * Executes the provided DSL (Domain-Specific Language) code within the context of the current InteractionHook.
 * If a PermissionException is thrown during the execution, an error message is sent as an embed to the original message's channel.
 *
 * @param dsl The DSL code to be executed.
 */
fun <T> InteractionHook.withTestPermission(dsl: InteractionHook.() -> T) {
    try {
        dsl()
    }
    catch(e: PermissionException) {
        getItsLogger().severe("PermissionException occurred during test permission execution: $e")
        val languageCode = ServerSettingsCache.get(this.interaction.guild!!.id).language
        val embed = EmbedBuilder()
            .setTitle("${TranslationCache.get(languageCode, "modals.errors.permissionCheck.title")} :x:")
            .setDescription("""
                    ${TranslationCache.get(languageCode, "modals.errors.permissionCheck.description")}
                    ```${e.permission}```
                """.trimIndent())
            .setColor(0xFF3333)
            .build()

        if(interaction.isAcknowledged) {
            this.editOriginal("").setEmbeds(
                embed
            ).setComponents().queue()
            return
        }

    }
}

/**
 * Executes the given DSL within a test permission context, catching any PermissionExceptions that occur and displaying an error message with the corresponding permission.
 *
 * @param dsl The DSL to be executed within the test permission context.
 * @param T The type of the return value of the DSL.
 */
fun <T> IReplyCallback.withTestPermission(dsl: IReplyCallback.() -> T) {
    try {
        dsl()
    }
    catch(e: PermissionException) {
        getItsLogger().severe("PermissionException occurred during test permission execution: $e")
        val languageCode = ServerSettingsCache.get(guild!!.id).language
        val embed = EmbedBuilder()
            .setTitle("${TranslationCache.get(languageCode, "modals.errors.permissionCheck.title")} :x:")
            .setDescription("""
                    ${TranslationCache.get(languageCode, "modals.errors.permissionCheck.description")}
                    ```${e.permission}```
                """.trimIndent())
            .setColor(0xFF3333)
            .build()

        if(isAcknowledged) {
            hook.editOriginal("").setEmbeds(
                embed
            ).setComponents().queue()
            return
        }

        replyEmbeds(
            embed
        ).setEphemeral(true).queue()
    }
}

fun <T> MessageChannel.withTestPermission(dsl: MessageChannel.() -> T) {
    try {
        dsl()
    }
    catch(e: PermissionException) {
        getItsLogger().severe("PermissionException occurred during test permission execution: $e")
        val languageCode = tryOrNull { this as? GuildMessageChannel }?.let { ServerSettingsCache.get(it.guild.id).language } ?: "en-US"
        val embed = EmbedBuilder()
            .setTitle("${TranslationCache.get(languageCode, "modals.errors.permissionCheck.title")} :x:")
            .setDescription("""
                    ${TranslationCache.get(languageCode, "modals.errors.permissionCheck.description")}
                    ```${e.permission}```
                """.trimIndent())
            .setColor(0xFF3333)
            .build()

        sendMessageEmbeds(
            embed
        ).queue()
    }
}

/**
 * Returns the DiscordLocale enum value based on the provided language code.
 *
 * @param languageCode The language code to determine the Discord locale.
 * @return The DiscordLocale enum value corresponding to the language code.
 */
fun getDiscordLocale(languageCode: String): DiscordLocale {
    val discordLocales = DiscordLocale.entries.find { languageCode.lowercase().contains(it.locale.lowercase()) }
    return discordLocales ?: DiscordLocale.ENGLISH_US
}

/**
 * Formats a nullable mention check.
 *
 * @param mention The mention string to be checked.
 * @return The formatted string indicating whether the mention is present or not.
 */
fun formatNullableMentionCheck(mention: String?): String {
    return if (mention != null) "✅ [ $mention ]" else "❌"
}

fun checkIfAlreadyTraveling(
    replyCallback: IReplyCallback,
    languageCode: String
): Boolean {
    val travel = TravelManager.get(replyCallback.user.id)
    if (travel != null) {
        replyCallback.reply(
            TranslationCache.get(
                languageCode, "town.menu.travel.already_traveling", mapOf(
                    "destination" to travel.connection.name,
                )
            ).toString()
        ).setEphemeral(true).queue()
        return true
    }
    return false
}


/**
 * Sends a system message to the guild.
 *
 * @param message The message to be sent.
 */
fun Guild.sendSystemMessage(message: String) {
    val serverSettings = ServerSettingsCache.get(id)
    val channel = serverSettings.systemAnnouncement.getChannelOrNull(this) ?: return
    channel.sendMessage(message).queue()
}


/**
 * Sends a translated system message to the guild.
 *
 * @param messageKey The key identifying the message to be sent.
 * @param placeholder The placeholders to be replaced in the message.
 */
fun Guild.sendTranslatedSystemMessage(messageKey: String, placeholder: Map<String, Any?> = emptyMap()) {
    val serverSettings = ServerSettingsCache.get(id)
    val message = TranslationCache.get(serverSettings.language, messageKey, placeholder).toString()
    val channel = serverSettings.systemAnnouncement.getChannelOrNull(this) ?: return
    channel.sendMessage(message).queue()
}