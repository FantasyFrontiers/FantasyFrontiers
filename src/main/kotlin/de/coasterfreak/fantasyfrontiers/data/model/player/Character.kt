package de.coasterfreak.fantasyfrontiers.data.model.player

import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.model.guild.GuildRank
import de.coasterfreak.fantasyfrontiers.data.model.guild.Guilds
import de.coasterfreak.fantasyfrontiers.data.model.town.Town
import kotlinx.serialization.Serializable

/**
 * Represents a character in the game.
 *
 * @property firstName The first name of the character.
 * @property lastName The last name of the character.
 * @property skinID The ID of the character's skin.
 * @property nobleTitle The noble title of the character (if applicable).
 * @property money The amount of money the character has.
 * @property stats The stats of the character.
 * @property skills The skills of the character along with their levels.
 * @property location The current location of the character.
 * @property guildRanks The ranks of the character in different guilds.
 */
@Serializable
data class Character(
    val firstName: String,
    val lastName: String,
    val skinID: String,
    val nobleTitle: NobleTitle?,
    val money: Long,
    // val job: Job,
    val stats: Stats,
    val skills: Map<Skill, Long>,
    val location: Town = TownCache.get("MistMeadow"), // The smallest town in the game
    val guildRanks: Map<Guilds, GuildRank> = emptyMap(),
)
