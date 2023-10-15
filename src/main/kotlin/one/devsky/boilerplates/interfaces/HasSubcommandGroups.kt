package one.devsky.boilerplates.interfaces

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

interface HasSubcommandGroups {
    fun getChoices() : List<SubcommandGroupData>
}