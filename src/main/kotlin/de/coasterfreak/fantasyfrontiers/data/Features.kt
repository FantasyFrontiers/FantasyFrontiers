package de.coasterfreak.fantasyfrontiers.data

import kotlinx.serialization.Serializable

@Serializable
data class Features(
    val capital: Boolean = false,
    val citadel: Boolean = false,
    val plaza: Boolean = false,
    val port: Boolean = false,
    val shanty: Boolean = false,
    val temple: Boolean = false,
    val walls: Boolean = false
)