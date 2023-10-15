package one.devsky.boilerplates.annotations

annotation class SlashCommand(
    val name: String,
    val description: String,
    val globalCommand: Boolean = false,
    val guilds: Array<String> = []
)
