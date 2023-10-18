package de.coasterfreak.fantasyfrontiers.data.model.guild

import kotlinx.serialization.Serializable

/**
 * Represents the available guilds in the game.
 */
@Serializable
enum class Guilds(val color: Int) {

    MERCHANTS_GUILD(0xfad390),
    ADVENTURERS_GUILD(0xb8e994),
    BLACKSMITHS_GUILD(0x2bcbba),
    HERBOLOGIES_GUILD(0xBDC581);
}