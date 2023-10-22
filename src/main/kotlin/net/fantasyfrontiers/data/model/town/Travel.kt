package net.fantasyfrontiers.data.model.town

import net.fantasyfrontiers.data.model.player.Character
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.model.items.Item
import net.fantasyfrontiers.data.model.items.ItemStack
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
        if (Math.random() < 0.01) {
            lastInterruption = Calendar.now()
            val amount = (Math.random() * 10).toInt() + 1
            val randomItem = if (Math.random() < 0.8) ItemStack(Item.PEBBLE, amount) else ItemStack(Item.BRONZE_COIN, amount)
            val translationItem = TranslationCache.get(character.language, randomItem.item.getTranslatableName()).toString()
            val translationDesc = TranslationCache.get(character.language, randomItem.item.getTranslatableDescription()).toString()

            val drops = character.inventory.addItem(randomItem)

            if (drops > 0) {
                val translationDrops = TranslationCache.get(character.language, "travel.found.not_enough_space", mapOf(
                    "item" to translationItem,
                    "amount" to amount.toString(),
                    "dropAmount" to drops.toString()
                )).toString()
                threadChannel.sendMessage(translationDrops).queue()
                return
            }


            val foundTranslation = TranslationCache.get(character.language, "travel.found.item", mapOf(
                "item" to translationItem,
                "desc" to translationDesc,
                "amount" to amount.toString()
            )).toString()

            threadChannel.sendMessage(foundTranslation).queue()
            CharacterCache.put(character)
        }
    }


}
