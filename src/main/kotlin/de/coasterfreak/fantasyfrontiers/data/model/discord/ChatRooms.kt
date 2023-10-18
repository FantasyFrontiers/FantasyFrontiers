package de.coasterfreak.fantasyfrontiers.data.model.discord

import kotlinx.serialization.Serializable

@Serializable
enum class ChatRoomDiscordLocation {
    NONE, CHANNEL, THREAD
}

@Serializable
enum class ChatRoomType(val defaultLocation: ChatRoomDiscordLocation) {
    SYSTEM(ChatRoomDiscordLocation.NONE),
    GLOBAL(ChatRoomDiscordLocation.NONE),
    TOWN(ChatRoomDiscordLocation.NONE),
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