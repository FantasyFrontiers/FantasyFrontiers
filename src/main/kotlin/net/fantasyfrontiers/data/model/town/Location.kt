package net.fantasyfrontiers.data.model.town

import dev.fruxz.ascend.extension.getResourceOrNull
import kotlinx.serialization.Serializable

/**
 * Represents a location in a town.
 *
 * @property town The town where the location is located.
 * @property specialLocation The special location within the town.
 */
@Serializable
data class Location(val town: Town, val specialLocation: SpecialLocation = SpecialLocation.OUTSIDE) {

    val locationImage by lazy { getResourceOrNull("assets/locations/${specialLocation.name.lowercase()}.png") ?: town.townMapImage }


    /**
     * Retrieves the list of special locations that can be traveled to from the current special location.
     *
     * @return The list of special locations.
     */
    fun travelLocations(): List<SpecialLocation> {
        when (specialLocation) {
            SpecialLocation.OUTSIDE -> return if(SpecialLocation.GATE.isThere(town)) listOf(SpecialLocation.GATE) else listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.GATE -> return listOf(SpecialLocation.OUTSIDE, SpecialLocation.MARKETPLACE)
            SpecialLocation.MARKETPLACE -> {
                val list = mutableListOf(SpecialLocation.OUTSIDE)
                if(SpecialLocation.GATE.isThere(town)) list.add(SpecialLocation.GATE)
                if(SpecialLocation.TAVERN.isThere(town)) list.add(SpecialLocation.TAVERN)
                if(SpecialLocation.BLACKSMITH.isThere(town)) list.add(SpecialLocation.BLACKSMITH)
                if(SpecialLocation.HERB_GARDEN.isThere(town)) list.add(SpecialLocation.HERB_GARDEN)
                if(SpecialLocation.TEMPLE.isThere(town)) list.add(SpecialLocation.TEMPLE)
                if(SpecialLocation.PORT.isThere(town)) list.add(SpecialLocation.PORT)
                if(SpecialLocation.CASTLE.isThere(town)) list.add(SpecialLocation.CASTLE)
                if(SpecialLocation.MERCHANTS_GUILD.isThere(town)) list.add(SpecialLocation.MERCHANTS_GUILD)
                if(SpecialLocation.ADVENTURERS_GUILD.isThere(town)) list.add(SpecialLocation.ADVENTURERS_GUILD)
                if(SpecialLocation.BLACKSMITHS_GUILD.isThere(town)) list.add(SpecialLocation.BLACKSMITHS_GUILD)
                if(SpecialLocation.HERB_GUILD.isThere(town)) list.add(SpecialLocation.HERB_GUILD)
                return list
            }
            SpecialLocation.TAVERN -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.BLACKSMITH -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.HERB_GARDEN -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.TEMPLE -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.PORT -> return if(SpecialLocation.GATE.isThere(town)) listOf(SpecialLocation.GATE) else listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.CASTLE -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.MERCHANTS_GUILD -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.ADVENTURERS_GUILD -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.BLACKSMITHS_GUILD -> return listOf(SpecialLocation.MARKETPLACE)
            SpecialLocation.HERB_GUILD -> return listOf(SpecialLocation.MARKETPLACE)
        }
    }

    override fun toString(): String {
        return town.name + if (specialLocation != SpecialLocation.OUTSIDE) ":${
            specialLocation.name}" else ""
    }


    companion object {

        /**
         * Parses a string value and returns a Location object.
         *
         * @param value The input string value representing a location. If the value contains a ":", it is expected to have the format "{townName}:{specialLocationName}". If the value does not contain a ":", it is treated as the town name only.
         * @return The Location object parsed from the input value.
         * @throws Exception If the town or special location specified in the input value is not found.
         */
        fun fromString(value: String): Location {
            if (value.contains(":")) {
                val townName = value.substring(0, value.indexOf(":"))
                val specialLocationName = value.substring(value.indexOf(":") + 1)
                val town = Towns.getByName(townName)
                val specialLocation = SpecialLocation.entries.find { it.name.equals(specialLocationName, ignoreCase = true) }
                    ?: throw Exception("Special location $specialLocationName not found.")

                return Location(town, specialLocation)
            }

            return Location(Towns.getByName(value))
        }
    }
}



/**
 * Represents special locations that can be found in a town.
 *
 * @property isThere The lambda expression that determines if the special location is present in a town. It takes a [Town] object as a parameter and returns a [Boolean].
 */
@Serializable
enum class SpecialLocation(val emoji: String, val isThere: (Town) -> Boolean = { true }) {

    OUTSIDE("🌸"),
    GATE("<:castle_grey:1165944193991061514>", { it.features.walls }),
    MARKETPLACE("<:market:1164514485814444042>"),
    TAVERN("🍻", { it.features.shanty }),
    BLACKSMITH("<:anvil:1165947985423777854>", { it.features.blacksmith }),
    HERB_GARDEN("<:garden:1165948869687910522>", { it.features.herbGarden }),
    TEMPLE("⛪", { it.features.temple }),
    PORT("⚓", { it.features.port }),
    CASTLE("🏰", { it.features.capital }),
    MERCHANTS_GUILD("🐫", { it.features.merchantsGuild }),
    ADVENTURERS_GUILD("🐅", { it.features.adventurersGuild }),
    BLACKSMITHS_GUILD("🦬", { it.features.blacksmithsGuild }),
    HERB_GUILD("🦩", { it.features.herbologistsGuild });

}