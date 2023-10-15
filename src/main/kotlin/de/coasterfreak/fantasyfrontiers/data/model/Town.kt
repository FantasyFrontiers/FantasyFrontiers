package de.coasterfreak.fantasyfrontiers.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Town(
    val name: String,
    val coords: Coords = Coords(),
    val type: String = "Generic",
    val population: Long = 0,
    val features: Features = Features(),
    val connections: List<Connection> = emptyList(),
)