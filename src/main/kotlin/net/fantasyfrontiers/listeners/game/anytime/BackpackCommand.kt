package net.fantasyfrontiers.listeners.game.anytime

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.fantasyfrontiers.annotations.SlashCommand
import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.cache.ServerSettingsCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.model.player.Character
import net.fantasyfrontiers.utils.functions.whiteSpaceChar

@SlashCommand("backpack", "Shows the contents of your backpack.")
class BackpackCommand : ListenerAdapter() {


    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) = with(event) {
        if (name != "backpack") return

        val character = CharacterCache.get(user.id)

        if (character == null) {
            var languageCode = "en-US"
            if (isFromGuild) {
                languageCode = ServerSettingsCache.get(guild!!.id).language
            }

            reply(
                TranslationCache.get(languageCode, "modals.errors.charSetup.noCharacter").toString()
            ).setEphemeral(true).queue()
            return@with
        }

        showBackpack(character)
    }

    private fun IReplyCallback.showBackpack(character: Character, page: Int = 1) {
        val languageCode = character.language
        val items = character.inventory.getDistinctItems()

        // maxOf 15 items per page
        val pages = items.chunked(15)

        if (page > pages.size) {
            reply(
                TranslationCache.get(languageCode, "modals.errors.backpack.invalidPage").toString()
            ).setEphemeral(true).queue()
            return
        }

        val itemsOnPage = pages[page - 1]
        val itemDescriptions = itemsOnPage.mapIndexed { index, itemStack ->
            "*${((page - 1) * 15) + (index + 1)}.* ${TranslationCache.get(languageCode, itemStack.item.getTranslatableName())} **x${itemStack.amount}**"
        }

        val embed = EmbedBuilder()
            .setTitle(TranslationCache.get(languageCode, "backpack.title", mapOf(
                "page" to page,
                "maxPage" to pages.size
            )).toString())
            .setDescription(
                itemDescriptions.joinToString("\n")
            )
            .setColor(0xccae62)
            .build()

        val actionRow = ActionRow.of(
            Button.secondary("ff-backpack-${page-1}", TranslationCache.get(languageCode, "backpack.previous").toString())
                    .withEmoji(Emoji.fromFormatted("◀️")).withDisabled(page <= 1)
            ,
            Button.secondary("ff-backpack-${page+1}", TranslationCache.get(languageCode, "backpack.next").toString())
                    .withEmoji(Emoji.fromFormatted("▶️")).withDisabled(page >= pages.size)
        )

        if (isAcknowledged) {
            hook.editOriginalEmbeds(embed).setComponents(actionRow).queue()
        } else {
            replyEmbeds(embed).setComponents(actionRow).setEphemeral(true).queue()
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        if (!event.componentId.startsWith("ff-backpack-")) return

        val character = CharacterCache.get(event.user.id) ?: return

        val page = event.componentId.substringAfter("ff-backpack-").toIntOrNull() ?: return

        event.deferEdit().queue()

        showBackpack(character, page)
    }

}