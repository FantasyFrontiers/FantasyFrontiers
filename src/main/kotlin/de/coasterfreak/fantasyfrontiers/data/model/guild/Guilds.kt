package de.coasterfreak.fantasyfrontiers.data.model.guild

import kotlinx.serialization.Serializable

/**
 * Represents the available guilds in the game.
 */
@Serializable
enum class Guilds {
    MERCHANTS_GUILD,
    ADVENTURERS_GUILD,
    BLACKSMITHS_GUILD,
    HEALERS_GUILD,
}