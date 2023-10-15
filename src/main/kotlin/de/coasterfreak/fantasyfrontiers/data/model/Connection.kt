package de.coasterfreak.fantasyfrontiers.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val name: String,
    val distance: Int = 0,
)