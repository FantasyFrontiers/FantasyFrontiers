package de.coasterfreak.fantasyfrontiers.data.model.discord

import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.Permission

@Serializable
enum class ChatRoomDiscordLocation {
    NONE, CHANNEL, THREAD
}

@Serializable
enum class ChatRoomType(val defaultLocation: ChatRoomDiscordLocation, val disallowPermissions: List<Permission> = emptyList(), val allowPermissions: List<Permission> = emptyList()) {
    SYSTEM(ChatRoomDiscordLocation.NONE, listOf(Permission.MESSAGE_SEND)),
    MERCHANTS_GUILD(ChatRoomDiscordLocation.NONE),
    ADVENTURERS_GUILD(ChatRoomDiscordLocation.NONE),
    BLACKSMITHS_GUILD(ChatRoomDiscordLocation.NONE),
    HERBOLOGIES_GUILD(ChatRoomDiscordLocation.NONE),
}

@Serializable
data class DiscordChatRoom (
    val chatRoomType: ChatRoomType,
    val chatRoomDiscordLocation: ChatRoomDiscordLocation = chatRoomType.defaultLocation,
    val chatRoomChannelId: String? = null
)