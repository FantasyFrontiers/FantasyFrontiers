package net.fantasyfrontiers.interfaces

import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * This interface represents an object that has options. Implementing classes need to provide
 * implementation for the method `getOptions()` which returns a list of [OptionData] objects.
 */
interface HasOptions {
    fun getOptions() : List<OptionData>
}