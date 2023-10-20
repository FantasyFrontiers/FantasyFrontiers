package net.fantasyfrontiers.data.model.discord

import net.fantasyfrontiers.data.model.guild.Guilds
import kotlinx.serialization.Serializable

/**
 * Represents the roles assigned to a guild in the game.
 *
 * @property guild The guild to which the role belongs.
 * @property roleId The ID of the role.
 */
@Serializable
data class GuildRole(
    val guild: Guilds,
    val roleId: String
)
