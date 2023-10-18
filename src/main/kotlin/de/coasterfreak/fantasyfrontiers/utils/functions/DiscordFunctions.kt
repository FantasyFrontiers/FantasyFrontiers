package de.coasterfreak.fantasyfrontiers.utils.functions

import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ComponentInteraction

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
        val languageCode = ServerSettingsCache.get(this.interaction.guild!!.id).language

        this.editOriginal("").setEmbeds(
            EmbedBuilder()
                .setTitle("${TranslationCache.get(languageCode, "modals.errors.permissionCheck.title")} :x:")
                .setDescription("""
                    ${TranslationCache.get(languageCode, "modals.errors.permissionCheck.description")}
                    ```${e.permission}```
                """.trimIndent())
                .setColor(0xFF3333)
                .build()
        ).queue()
    }
}

/**
 * Executes the given DSL within a test permission context, catching any PermissionExceptions that occur and displaying an error message with the corresponding permission.
 *
 * @param dsl The DSL to be executed within the test permission context.
 * @param T The type of the return value of the DSL.
 */
fun <T> ComponentInteraction.withTestPermission(dsl: InteractionHook.() -> T) {
    hook.withTestPermission(dsl)
}

/**
 * Executes the given DSL within a test permission context, catching any PermissionExceptions that occur and displaying an error message with the corresponding permission.
 *
 * @param dsl The DSL to be executed within the test permission context.
 * @param T The type of the return value of the DSL.
 */
fun <T> IReplyCallback.withTestPermission(dsl: InteractionHook.() -> T) {
    hook.withTestPermission(dsl)
}


fun getDiscordLocale(languageCode: String): DiscordLocale {
    val discordLocales = DiscordLocale.entries.find { languageCode.lowercase().contains(it.locale.lowercase()) }
    return discordLocales ?: DiscordLocale.ENGLISH_US
}