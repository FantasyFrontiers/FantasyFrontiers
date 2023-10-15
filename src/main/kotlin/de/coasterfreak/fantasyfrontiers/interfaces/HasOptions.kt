package de.coasterfreak.fantasyfrontiers.interfaces

import net.dv8tion.jda.api.interactions.commands.build.OptionData

interface HasOptions {
    fun getOptions() : List<OptionData>
}