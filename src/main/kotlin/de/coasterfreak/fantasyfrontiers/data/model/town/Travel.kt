package de.coasterfreak.fantasyfrontiers.data.model.town

import de.coasterfreak.fantasyfrontiers.data.model.player.Character
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Travel (
    val character: Character,
    val connection: Connection,
    val threadChannel: ThreadChannel,
    val start: Calendar,
    val end: Calendar = start + connection.getTravelDuration(character),
    var lastInterruption: Calendar? = null,
) {

    /**
     * This method is used to test for a random encounter during travel.
     * If the character has been interrupted within the last 30 seconds, no random encounter will occur.
     * If the random number generated is less than 0.1, a random encounter will occur.
     * The last interruption time will be updated and a message will be sent to the thread channel.
     */
    fun testForRandomEncounter() {
        if (lastInterruption != null && lastInterruption!! + 30.seconds > Calendar.now()) return
        if (Math.random() < 0.1) {
            lastInterruption = Calendar.now()
            threadChannel.sendMessage("You have been interrupted by a random encounter!").queue()
        }
    }


}
