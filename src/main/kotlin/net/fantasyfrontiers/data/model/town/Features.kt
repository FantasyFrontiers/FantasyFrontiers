package net.fantasyfrontiers.data.model.town

import kotlinx.serialization.Serializable

/**
 * Represents a set of features that a town can have.
 *
 * @property capital A boolean flag indicating if the town has a capital.
 * @property citadel A boolean flag indicating if the town has a citadel.
 * @property plaza A boolean flag indicating if the town has a plaza.
 * @property port A boolean flag indicating if the town has a port.
 * @property shanty A boolean flag indicating if the town has a shanty.
 * @property temple A boolean flag indicating if the town has a temple.
 * @property walls A boolean flag indicating if the town has walls.
 */
@Serializable
data class Features(
    val capital: Boolean = false,
    val citadel: Boolean = false,
    val port: Boolean = false,
    val shanty: Boolean = false,
    val temple: Boolean = false,
    val walls: Boolean = false,
    val blacksmith: Boolean = false,
    val herbGarden: Boolean = false,
    val merchantsGuild: Boolean = false,
    val adventurersGuild: Boolean = false,
    val blacksmithsGuild: Boolean = false,
    val herbologistsGuild: Boolean = false,
) {
    override fun toString(): String {
        return """Features(
    |   capital = $capital,
    |   citadel = $citadel,
    |   port = $port,
    |   shanty = $shanty,
    |   temple = $temple,
    |   walls = $walls,
    |   blacksmith = $blacksmith,
    |   herbGarden = $herbGarden,
    |   merchantsGuild = $merchantsGuild,
    |   adventurersGuild = $adventurersGuild,
    |   blacksmithsGuild = $blacksmithsGuild,
    |   herbologistsGuild = $herbologistsGuild
    |)""".trimMargin()
    }
}