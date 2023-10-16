package de.coasterfreak.fantasyfrontiers.data.model.discord

/**
 * Represents the settings for a server.
 *
 * @property guildID The ID of the server associated with the settings.
 * @property language The language code for the server. Default value is "en-US".
 */
data class ServerSettings(
    val guildID: String,
    val language: String = "en-US",
)
