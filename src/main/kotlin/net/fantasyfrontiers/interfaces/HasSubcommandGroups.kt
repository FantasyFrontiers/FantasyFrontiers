package net.fantasyfrontiers.interfaces

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

/**
 * This interface represents an object that has subcommand groups. Implementing classes must provide
 * an implementation for the method `getChoices()` which returns a list of [SubcommandGroupData] objects.
 */
interface HasSubcommandGroups {
    fun getChoices() : List<SubcommandGroupData>
}