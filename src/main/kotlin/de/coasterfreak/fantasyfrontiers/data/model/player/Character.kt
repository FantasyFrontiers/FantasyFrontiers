package de.coasterfreak.fantasyfrontiers.data.model.player

import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.model.town.Town
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

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

/**
 * A class representing a skill.
 *
 * @property name The name of the skill.
 * @property maxLevel The maximum level of the skill.
 * @property experienceRatio The experience ratio of the skill (default is 1.0).
 */
@Serializable
data class Skill(
    val name: String,
    val maxLevel: Int,
    val experienceRatio: Double = 1.0,
) {

    /**
     * Converts the given experience to the corresponding level based on the experience ratio.
     *
     * @param exp the experience to convert to level
     * @return the level corresponding to the given experience
     */
    fun experienceToLevel(exp: Long) = min((sqrt(exp.toDouble() / experienceRatio) / 2).toInt(), maxLevel)
    /**
     * Calculates the experience required to reach the specified level.
     *
     * @param level The level for which to calculate the experience.
     * @return The experience required to reach the specified level.
     */
    fun levelToExperience(level: Int) = (level * 2).toDouble().pow(2) * experienceRatio

    /**
     * The key used to retrieve the name for a skill.
     */
    val nameKey = "skill.$name"
    /**
     * The key used to retrieve the description for a skill.
     *
     * This key is used to retrieve the description for a specific skill. The description is a localized string
     * that provides information about the skill.
     *
     * The format of the key is "skill.<name>.description", where <name> is the name of the skill. This key is used
     * in conjunction with a localization library to retrieve the appropriate localized description for the skill.
     *
     * Example usage:
     * ```kotlin
     * val descriptionKey = "skill.$name.description"
     * val description = localizationLibrary.getLocalization(descriptionKey)
     * ```
     *
     * For example, if the `name` property of the skill is "programming", the description key would be "skill.programming.description".
     * The localization library would then retrieve the description for the "programming" skill in the appropriate language.
     */
    val descriptionKey = "skill.$name.description"
}

/**
 * Represents the statistics (stats) of a character.
 *
 * @property healthPoints The current health points (HP) of the character. Default value is 20.
 * @property manaPoints The current mana points (MP) of the character. Default value is 20.
 * @property strength The physical damage and carry weight of the character. Default value is 1.
 * @property vitality The physical defense and HP regeneration of the character. Default value is 1.
 * @property dexterity The physical accuracy and critical hit chance of the character. Default value is 1.
 * @property agility The physical evasion and movement speed of the character. Default value is 1.
 * @property intelligence The MP regeneration and learning speed of the character. Default value is 1.
 * @property magic The magical damage and MP cost of the character. Default value is 1.
 * @property charisma The conversation success rate of the character. Default value is 1.
 * @property reputation The conversation success rate, trading prices, and quest rewards of the character. Default value is 0.
 * @property luck The critical hit chance and item drop rate of the character. Default value is 1.
 *
 * @constructor Creates a new instance of the Stats class with the specified property values.
 */
@Serializable
data class Stats(
    // Base stats
    val healthPoints: Int = 20, // HP
    val manaPoints: Int = 20, // MP

    // Physical stats
    val strength: Int = 1, // Physical damage and carry weight
    val vitality: Int = 1, // Physical defense and HP regeneration
    val dexterity: Int = 1, // Physical accuracy and critical hit chance
    val agility: Int = 1, // Physical evasion and movement speed

    // Mental stats
    val intelligence: Int = 1, // MP regeneration and learning speed
    val magic: Int = 1, // Magical damage and MP cost

    // Social stats
    val charisma: Int = 1, // Conversation success rate
    val reputation: Int = 0, // Conversation success rate, trading prices and quest rewards

    // Extra stats
    val luck: Int = 1, // Critical hit chance and item drop rate
)

/**
 * Represents a noble title.
 *
 * @property nameKey The translation key used to retrieve the localized name of the noble title.
 * @property titleKey The translation key used to retrieve the localized title of the noble title.
 */
@Serializable
enum class NobleTitle {
    KNIGHT,
    BARON,
    COUNT,
    MARGRAVE,
    LANDGRAVE,
    DUKE,
    ELECTOR,
    GRAND_DUKE,
    ARCHDUKE,
    KING,
    EMPEROR;

    val nameKey = "noble.rank.${name.lowercase()}"

    // If now title translation is found, the name is used instead.
    val titleKey = "noble.title.${name.lowercase()}"
}

/**
 * Represents the context of a conversation.
 */
@Serializable
enum class ConversationContext {
    FRIENDSHIP,
    FORMAL
}

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

/**
 * Represents a guild rank with an associated experience requirement.
 *
 * @property xpNeeded The experience required to achieve this guild rank.
 */
@Serializable
enum class GuildRank(val xpNeeded: Long) {
    G(0),
    F(100),
    E(500),
    D(1200),
    C(2500),
    B(5000),
    A(10000),
    S(25000),
    SS(100000)
}