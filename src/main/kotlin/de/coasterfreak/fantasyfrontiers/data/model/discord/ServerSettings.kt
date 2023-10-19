package de.coasterfreak.fantasyfrontiers.data.model.discord

import kotlinx.serialization.Serializable

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
)
