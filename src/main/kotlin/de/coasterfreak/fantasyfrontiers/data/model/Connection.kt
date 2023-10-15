package de.coasterfreak.fantasyfrontiers.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a connection between two towns.
 *
 * @property name The name of the connected town.
 * @property distance The distance between the two towns.
 */
@Serializable
data class Connection(
    val name: String,
    val distance: Int = 0,
)