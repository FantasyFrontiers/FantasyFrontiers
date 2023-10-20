package net.fantasyfrontiers.data.model.player

import net.fantasyfrontiers.data.model.guild.Guilds
import net.fantasyfrontiers.data.model.town.Town
import net.fantasyfrontiers.data.model.town.Towns
import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.model.items.Inventory

/**
 * Represents a character in the game.
 *
 * This class holds information about a character, such as their name, stats, skills, location, and guild ranks.
 * The character's information is used to determine their abilities, progress, and interactions within the game world.
 *
 * @property discordClientID The ID of the Discord client associated with the character.
 * @property language The language code of the character's preferred language. Default value is "en-US".
 * @property firstName The first name of the character.
 * @property lastName The last name of the character.
 * @property nobleTitle The noble title of the character. Default value is null.
 * @property money The amount of money the character possesses. Default value is 0.
 * @property stats The statistics (stats) of the character. Default value is an instance of the Stats class with default values.
 * @property skills The skills of the character mapped to their corresponding experience. Default value is an empty map.
 * @property location The current location of the character. Default value is the "MistMeadow" town.
 * @property guildRanks The ranks of the character in different guilds. Default value is an empty map.
 */
@Serializable
data class Character(
    val discordClientID: String,
    val language: String = "en-US",
    val firstName: String,
    val lastName: String,
    // val skinID: String? = null,
    val nobleTitle: NobleTitle? = null,
    val money: Long = 0,
    // val job: Job,

    val inventory: Inventory = Inventory(),

    /**
     * The statistics (stats) of the character.
     * These are only the base statistics of the character, which are not modified by skills or experience.
     * To get the modified statistics, use the [skilledStats] property.
     * @see Stats
     */
    val stats: Stats = Stats(),
    val skills: Map<Skill, Long> = emptyMap(),
    val location: Town = Towns.MISTMEADOW, // The smallest town in the game
    val guildRanks: Map<Guilds, Long> = emptyMap(),
) {


    /**
     * Represents the skilled statistics of a character, which are modified based on the character's skills and experience.
     *
     * The skilled statistics are calculated by applying the modifications from each skill to the base statistics.
     * The modifications are determined by the experience level of each skill.
     *
     * @property stats The base statistics of the character.
     * @property skills The skills and their corresponding experience levels.
     * @return The modified statistics based on the character's skills and experience.
     */
    val skilledStats: Stats
        get() {
            var modifiedStats = stats
            skills.forEach { (skill, experience) ->
                modifiedStats = skill.modifyStats.invoke(skill, modifiedStats, experience)
            }
            return modifiedStats
        }

}
