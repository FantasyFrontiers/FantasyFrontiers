package de.coasterfreak.fantasyfrontiers.data

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val name: String,
    val distance: Int = 0,
)