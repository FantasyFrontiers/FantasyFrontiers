package de.coasterfreak.fantasyfrontiers.data.model.town

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
    val features: Features = Features(),
    val connections: List<Connection> = emptyList(),
)