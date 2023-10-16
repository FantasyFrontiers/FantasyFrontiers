package de.coasterfreak.fantasyfrontiers.data.model.player

import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

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