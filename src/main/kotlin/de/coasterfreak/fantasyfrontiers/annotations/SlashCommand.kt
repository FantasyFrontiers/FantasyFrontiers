package de.coasterfreak.fantasyfrontiers.annotations

annotation class SlashCommand(
    val name: String,
    val description: String,
    val adminOnly: Boolean = false
)
