package net.fantasyfrontiers.data.model.town

import net.fantasyfrontiers.data.model.player.Character
import dev.fruxz.ascend.extension.time.millisecond
import dev.fruxz.ascend.extension.time.second
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

const val walkSpeed = 6 // km/s

/**
 * Represents a connection between two towns.
 *
 * @property name The name of the connected town.
 * @property distance The distance between the two towns.
 */
@Serializable
data class Connection(
    val name: String,
    val distance: Int = 0,
) {

    /**
     * Calculates the travel duration for a character to traverse a connection between two towns.
     * The travel duration is based on the character's walk speed and agility stat.
     *
     * @param character The character for which to calculate the travel duration.
     * @return The travel duration as a Duration object.
     */
    fun getTravelDuration(character: Character): Duration {
            return (distance / (walkSpeed + (character.skilledStats.agility / 10))).seconds
        }

    /**
     * Calculates the estimated time of arrival (ETA) for a character.
     *
     * @param character The character for which to calculate the ETA.
     * @return The ETA in the format "<t:timestamp:R>", where 'timestamp' is the calculated timestamp in seconds.
     */
    fun getEta(character: Character): String {
        val eta = Calendar.now() + getTravelDuration(character)
        return "<t:${(eta.timeInMilliseconds / 1000)}:R>"
    }

    override fun toString(): String {
        return """Connection(
    |   name = "$name",
    |   distance = $distance
    |)""".trimMargin()
    }
}