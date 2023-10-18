package de.coasterfreak.fantasyfrontiers.data.db.discord

import de.coasterfreak.fantasyfrontiers.data.model.discord.ChatRoomDiscordLocation
import de.coasterfreak.fantasyfrontiers.data.model.discord.ChatRoomType
import de.coasterfreak.fantasyfrontiers.data.model.discord.DiscordChatRoom
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents the database table "discord_chat_rooms" for storing information about chat rooms in a Discord guild.
 * Inherits from the abstract class `Table`.
 *
 * Properties:
 * - `guildId`: A varchar column representing the guild ID of the chat room. It references the `guildID` column
 *   in the `ServerSettingsTable` and has a length limit of 24 characters. It has a foreign key constraint with
 *   `ReferenceOption.CASCADE` onDelete behavior.
 * - `chatRoomType`: An enumeration column representing the type of the chat room. It accepts values from the
 *   `ChatRoomType` enum class.
 * - `chatRoomDiscordLocation`: An enumeration column representing the Discord location of the chat room. It
 *   accepts values from the `ChatRoomDiscordLocation` enum class.
 * - `chatRoomChannelId`: A nullable varchar column representing the channel ID of the chat room. It has a length
 *   limit of 24 characters.
 *
 * Primary Key:
 * The primary key consists of the `guildId` and `chatRoomType` columns.
 */
object ChatRoomTable: Table("discord_chat_rooms") {

    val guildId = varchar("guild_id", 24).references(ServerSettingsTable.guildID, onDelete = ReferenceOption.CASCADE)
    val chatRoomType = enumeration("chat_room_type", ChatRoomType::class)
    val chatRoomDiscordLocation = enumeration("chat_room_discord_location", ChatRoomDiscordLocation::class)
    val chatRoomChannelId = varchar("chat_room_channel_id", 24).nullable()

    override val primaryKey = PrimaryKey(guildId, chatRoomType)
}

/**
 * Retrieves the list of chat rooms associated with the given guild ID.
 *
 * @param guildId The ID of the guild for which to retrieve the chat rooms.
 * @return A list of [DiscordChatRoom] objects representing the chat rooms.
 */
fun getChatRooms(guildId: String): List<DiscordChatRoom> = transaction {
    return@transaction ChatRoomTable.select(ChatRoomTable.guildId eq guildId).map { row ->
        DiscordChatRoom(
            chatRoomType = row[ChatRoomTable.chatRoomType],
            chatRoomDiscordLocation = row[ChatRoomTable.chatRoomDiscordLocation],
            chatRoomChannelId = row[ChatRoomTable.chatRoomChannelId]
        )
    }
}

/**
 * Updates the chat room information for a specific guild in the Discord server.
 *
 * @param guildId The ID of the guild to update the chat room for.
 * @param chatRoom The chat room object containing the updated information.
 */
fun updateChatRoom(guildId: String, chatRoom: DiscordChatRoom) = transaction {
    ChatRoomTable.replace {
        it[ChatRoomTable.guildId] = guildId
        it[ChatRoomTable.chatRoomType] = chatRoom.chatRoomType
        it[ChatRoomTable.chatRoomDiscordLocation] = chatRoom.chatRoomDiscordLocation
        it[ChatRoomTable.chatRoomChannelId] = chatRoom.chatRoomChannelId
    }
}

/**
 * Updates the chat rooms for a specific guild.
 *
 * @param guildId The ID of the guild.
 * @param chatRooms The list of chat rooms to be updated.
 */
fun updateChatRooms(guildId: String, chatRooms: List<DiscordChatRoom>) = transaction {
    ChatRoomTable.batchReplace(chatRooms) {
        this[ChatRoomTable.guildId] = guildId
        this[ChatRoomTable.chatRoomType] = it.chatRoomType
        this[ChatRoomTable.chatRoomDiscordLocation] = it.chatRoomDiscordLocation
        this[ChatRoomTable.chatRoomChannelId] = it.chatRoomChannelId
    }
}