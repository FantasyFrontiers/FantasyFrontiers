package net.fantasyfrontiers.data.model.town

import kotlinx.serialization.Serializable
import net.fantasyfrontiers.data.model.player.Character


/**
 * Represents actions that can be performed in different locations.
 *
 * Each action is associated with a list of special locations where it can be performed, and an optional test
 * to determine if the action is enabled for a character in a specific special location.
 *
 * @property specialLocations The special locations where the action can be performed.
 * @property testEnabled The test to determine if the action is enabled for a character in a specific special location.
 */
@Serializable
enum class LocationActions(val emoji: String, val specialLocations: List<SpecialLocation> = emptyList(), val testEnabled: (Character, SpecialLocation) -> Boolean = { _, _ -> false }) {


    QUEST_BOARD("📖",
        listOf(
            SpecialLocation.ADVENTURERS_GUILD, SpecialLocation.BLACKSMITHS_GUILD, SpecialLocation.HERB_GUILD, SpecialLocation.MERCHANTS_GUILD
        ), { character, specialLocation ->
            when (specialLocation) {
                SpecialLocation.ADVENTURERS_GUILD -> character.isInAdventurersGuild
                SpecialLocation.BLACKSMITHS_GUILD -> character.isInBlacksmithsGuild
                SpecialLocation.HERB_GUILD -> character.isInHerbologiesGuild
                SpecialLocation.MERCHANTS_GUILD -> character.isInMerchantsGuild
                else -> false
            }
        }
    ),
    JOIN_GUILD("📜",
        listOf(
            SpecialLocation.ADVENTURERS_GUILD, SpecialLocation.BLACKSMITHS_GUILD, SpecialLocation.HERB_GUILD, SpecialLocation.MERCHANTS_GUILD
        ), { character, specialLocation ->
            when (specialLocation) {
                SpecialLocation.ADVENTURERS_GUILD -> !character.isInAdventurersGuild
                SpecialLocation.BLACKSMITHS_GUILD -> !character.isInBlacksmithsGuild
                SpecialLocation.HERB_GUILD -> !character.isInHerbologiesGuild
                SpecialLocation.MERCHANTS_GUILD -> !character.isInMerchantsGuild
                else -> false
            }
        }
    ),
    HARVEST_GARDEN("🌾",
        listOf(
            SpecialLocation.HERB_GARDEN
        ), { character, _ -> character.isInHerbologiesGuild }
    ),
    BUY("<:buy:1166306884446330900>",
        listOf(
            SpecialLocation.MARKETPLACE
        )
    ),
    SELL("<:sell:1166306911835148288>",
        listOf(
            SpecialLocation.MARKETPLACE
        )
    );


    companion object {

        /**
         * Retrieves a list of location actions that can be performed at the given special location.
         *
         * @param specialLocation The special location for which to retrieve the actions.
         * @return A list of location actions that can be performed at the given special location.
         */
        @JvmStatic
        fun getActions(specialLocation: SpecialLocation): List<LocationActions> {
            return entries.filter { it.specialLocations.contains(specialLocation) }
        }

    }
}