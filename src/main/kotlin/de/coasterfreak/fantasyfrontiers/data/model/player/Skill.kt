package de.coasterfreak.fantasyfrontiers.data.model.player

import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.utils.extensions.asRomanNumeral
import kotlinx.serialization.Serializable
import kotlin.math.min
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
    val modifyStats: (Stats, Long) -> Stats,
) {

    /**
     * Converts the given experience to the corresponding level based on the experience ratio.
     *
     * @param exp the experience to convert to level
     * @return the level corresponding to the given experience
     */
    private fun experienceToLevel(exp: Long) = min((sqrt(exp.toDouble() / experienceRatio) / 2).toInt(), maxLevel)

    /**
     * The key used to retrieve the name for a skill.
     */
    private val nameKey = "skill.$name"
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
    private val descriptionKey = "skill.$name.description"

    /**
     * Returns the formatted name of a skill in the specified language and level.
     *
     * @param languageCode The language code of the translation as dash-combined ISO-639 (language) and ISO-3166 (country).
     * @param xp The experience of the skill.
     * @return The formatted name of the skill including the level if the maxLevel is greater than 1,
     * or just the name if the maxLevel is 1 or less.
     */
    fun getFormattedName(languageCode: String, xp: Long): String {
        val levelString = if (maxLevel <= 1) "" else " ${experienceToLevel(xp).asRomanNumeral()}"
        return "${TranslationCache.get(languageCode, nameKey)}$levelString"
    }

    /**
     * Retrieves the formatted description of a skill in the specified language.
     *
     * @param languageCode The language code of the translation as dash-combined ISO-639 (language) and ISO-3166 (country).
     * @return The formatted description of the skill in the specified language.
     */
    fun getFormattedDescription(languageCode: String): String {
        return TranslationCache.get(languageCode, descriptionKey).toString()
    }
}