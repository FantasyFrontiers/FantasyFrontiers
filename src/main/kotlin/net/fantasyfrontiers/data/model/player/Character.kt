package net.fantasyfrontiers.data.model.player

import net.fantasyfrontiers.data.model.guild.Guilds
import net.fantasyfrontiers.data.model.town.Town
import net.fantasyfrontiers.data.model.town.Towns
import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.db.player.saveCharacter
import net.fantasyfrontiers.data.model.guild.GuildCard
import net.fantasyfrontiers.data.model.items.Inventory
import net.fantasyfrontiers.data.model.town.Location
import net.fantasyfrontiers.data.model.town.SpecialLocation
import net.fantasyfrontiers.utils.extensions.containsGuild

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
    val location: Location = Location(Towns.MISTMEADOW, SpecialLocation.MARKETPLACE),
    val guildCards: List<GuildCard> = emptyList(),
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


    /**
     * Indicates whether the character is a member of the Adventurers Guild.
     *
     * @return `true` if the character is in the Adventurers Guild, `false` otherwise.
     */
    val isInAdventurersGuild: Boolean
        get() = guildCards.containsGuild(Guilds.ADVENTURERS_GUILD)

    /**
     * Indicates whether the character is a member of the Blacksmiths Guild.
     *
     * @return True if the character is a member of the Blacksmiths Guild, false otherwise.
     */
    val isInBlacksmithsGuild: Boolean
        get() = guildCards.containsGuild(Guilds.BLACKSMITHS_GUILD)

    /**
     * Represents whether a character is a member of the Herbologies Guild.
     *
     * @return `true` if the character is in the Herbologies Guild, `false` otherwise.
     */
    val isInHerbologiesGuild: Boolean
        get() = guildCards.containsGuild(Guilds.HERBOLOGIES_GUILD)

    /**
     * Represents whether a character is a member of the Merchants Guild.
     *
     * @return `true` if the character is a member of the Merchants Guild, `false` otherwise.
     */
    val isInMerchantsGuild: Boolean
        get() = guildCards.containsGuild(Guilds.MERCHANTS_GUILD)


    /**
     * Joins the specified guild.
     *
     * This method allows a character to join a guild. If the character is already a member of the guild, the method returns false and no changes are made.
     * Otherwise, the character is added to the guild's list of members, and the guild card is updated with a starting experience of 0.
     * The updated character is then saved in the cache and in the database.
     *
     * @param guild The guild to join.
     * @return true if the character successfully joins the guild, false if the character is already a member of the guild.
     */
    fun joinGuild(guild: Guilds): Boolean {
        if(guildCards.containsGuild(guild)) return false

        val updatedChar = this.copy(guildCards = guildCards + GuildCard(guild, 0))
        CharacterCache.put(updatedChar)
        saveCharacter(updatedChar)
        return true
    }
}
