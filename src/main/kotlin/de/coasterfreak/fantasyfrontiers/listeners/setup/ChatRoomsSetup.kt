package de.coasterfreak.fantasyfrontiers.listeners.setup

import de.coasterfreak.fantasyfrontiers.data.cache.ServerSettingsCache
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.data.model.discord.ChatRoomType
import de.coasterfreak.fantasyfrontiers.data.model.discord.ServerSettings
import de.coasterfreak.fantasyfrontiers.utils.functions.formatNullableMentionCheck
import dev.fruxz.ascend.extension.container.lastOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class ChatRoomsSetup : ListenerAdapter() {

    private val buttonHandlers = mapOf(
        "ff-setup-advanced-chatrooms" to this::chatRoomMenu,
    )

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        val btnHandler = buttonHandlers.lastOrNull { componentId.startsWith(it.key) }
        if (btnHandler == null) return
        if (!isFromGuild) return
        val serverSettings = ServerSettingsCache.get(guild!!.id)

        val args = componentId.replace(btnHandler.key, "").split("-").toTypedArray()

        btnHandler.value.invoke(event, serverSettings, args)
    }

    private fun chatRoomMenu(event: ButtonInteractionEvent, serverSettings: ServerSettings, args: Array<String>) = with(event) {
        val languageCode = serverSettings.language

        val chatRooms = ChatRoomType.entries.map { chatRoomType ->
            val chatRoomName = TranslationCache.get(languageCode, "chatroom.${chatRoomType.name.lowercase()}").toString()
            val chatRoom = serverSettings.discordChatRooms.find { chatRoom -> chatRoom.chatRoomType == chatRoomType }?.let {
                it.chatRoomChannelId?.let { it1 -> guild?.getGuildChannelById(it1) }?.asMention
            }
            Triple(chatRoomType, chatRoomName, chatRoom)
        }

        val chatRoomMenu = chatRooms.map { chatRoom ->
            "**${chatRoom.second}**: ${formatNullableMentionCheck(chatRoom.third)}"
        }

        if (isAcknowledged) {
            editMessageEmbeds(
                createRoomMenuEmbed(languageCode, chatRoomMenu)
            ).setComponents(createRoomMenuSelectMenu(chatRooms)).queue()
            return@with
        }

        replyEmbeds(
            createRoomMenuEmbed(languageCode, chatRoomMenu)
        ).setComponents(createRoomMenuSelectMenu(chatRooms)).setEphemeral(true).queue()
    }

    private fun createRoomMenuSelectMenu(chatRooms: List<Triple<ChatRoomType, String, String?>>) = ActionRow.of(
        StringSelectMenu.create("ff-setup-advanced-chatrooms-select")
            .apply {
                chatRooms.forEach { (chatRoomType, chatRoomNameName, _) ->
                    addOption(chatRoomNameName, chatRoomType.name.lowercase())
                }
            }
            .build()
    )

    private fun createRoomMenuEmbed(languageCode: String, chatRoomDescriptor: List<String>) = EmbedBuilder()
        .setTitle(TranslationCache.get(languageCode, "modals.advancedSetup.chatrooms.title").toString())
        .setDescription("*" + TranslationCache.get(languageCode, "modals.advancedSetup.chatrooms.description.before")
            .toString() + "*\n\n" +
            chatRoomDescriptor.joinToString("\n") + "\n\n" +
            "*" + TranslationCache.get(languageCode, "modals.advancedSetup.chatrooms.description.after")
                .toString() + "*"
        )
        .setColor(0x2b2d31)
        .build()

}