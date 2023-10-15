package one.devsky.boilerplates.annotations

annotation class MessageCommand(
    val name: String,
    val globalCommand: Boolean = false,
    val guilds: Array<String> = []
)