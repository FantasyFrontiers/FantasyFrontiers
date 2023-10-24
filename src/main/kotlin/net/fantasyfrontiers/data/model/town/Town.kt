package net.fantasyfrontiers.data.model.town

import dev.fruxz.ascend.extension.getResourceOrNull
import kotlinx.serialization.Serializable

/**
 * Represents a town with its name, coordinates, type, population, features, and connections.
 *
 * @property name The name of the town.
 * @property coords The coordinates of the town.
 * @property type The type of the town.
 * @property population The population of the town.
 * @property features The features of the town.
 * @property connections The connections of the town.
 */
@Serializable
data class Town(
    val name: String,
    val coords: Coords = Coords(),
    val type: String = "Generic",
    val population: Long = 0,
    val temperature: Int = 15,
    val humidity: Int = 50,
    val features: Features = Features(),
    val connections: List<Connection> = emptyList(),
) {

    val townMapImage by lazy { getResourceOrNull("assets/towns/${name}.png") ?: throw Exception("Town map image ${name}.png not found. Have you used the `images` command yet?") }

    override fun toString(): String {
        return """Town(
    |   name = "$name",
    |   coords = $coords,
    |   type = "$type",
    |   population = $population,
    |   temperature = $temperature,
    |   humidity = $humidity,
    |   features = $features,
    |   connections = listOf(${connections.joinToString(", ")})
    |)""".trimMargin()
    }
}