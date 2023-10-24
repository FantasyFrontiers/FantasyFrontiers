package net.fantasyfrontiers.data.model.discord

import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.model.guild.Guilds

/**
 * Represents the settings for a server.
 *
 * @property guildID The ID of the server associated with the settings.
 * @property language The language code for the server. Default value is "en-US".
 */
@Serializable
data class ServerSettings(
    val guildID: String,
    val language: String = "en-US",
    val systemAnnouncement: SystemAnnouncement = SystemAnnouncement(),
    val guildRoles: List<GuildRole> = emptyList()
) {


    fun getGuildRole(guild: Guilds): String? {
        return guildRoles.find { it.guild == guild }?.roleId
    }

}