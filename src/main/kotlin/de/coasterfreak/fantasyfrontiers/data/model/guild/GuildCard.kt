package de.coasterfreak.fantasyfrontiers.data.model.guild

import kotlinx.serialization.Serializable

/**
 * Represents a guild card for a player in a guild.
 *
 * @property guild The guild the player belongs to.
 * @property xp The amount of experience points the player has earned.
 */
@Serializable
data class GuildCard(
    val guild: Guilds,
    val xp: Long,
) {
    val rank: GuildRank
        get() = GuildRank.entries.last { xp >= it.xpNeeded }
}