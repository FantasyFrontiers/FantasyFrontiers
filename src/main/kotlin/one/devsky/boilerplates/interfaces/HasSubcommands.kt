package one.devsky.boilerplates.interfaces

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

interface HasSubcommands {
    fun getSubCommands() : List<SubcommandData>
}