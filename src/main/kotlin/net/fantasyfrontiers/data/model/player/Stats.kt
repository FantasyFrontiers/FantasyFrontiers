package net.fantasyfrontiers.data.model.player

import kotlinx.serialization.Serializable

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