package de.coasterfreak.fantasyfrontiers.data

import kotlinx.serialization.Serializable

@Serializable
data class Town(
    val name: String,
    val coords: Coords = Coords(),
    val type: String = "Generic",
    val population: Double = 0.0,
    val features: Features = Features(),
    val connections: List<Connection> = emptyList(),
)