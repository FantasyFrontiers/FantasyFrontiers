package net.fantasyfrontiers.listeners.game.town

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.db.player.saveCharacter
import net.fantasyfrontiers.data.model.town.Location
import net.fantasyfrontiers.utils.functions.checkIfAlreadyTraveling

class TownWalkListener : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        if(!componentId.startsWith("ff-town-menu-walk-")) return@with

        var character = CharacterCache.get(event.user.id) ?: return@with
        val languageCode = character.language

        if (checkIfAlreadyTraveling(event, languageCode)) return@with

        event.deferEdit().queue()

        val location = character.location
        val town = location.town

        val newSpecialLocation = componentId.removePrefix("ff-town-menu-walk-")
        val newLocation = Location.fromString("${town.name}:$newSpecialLocation")

        character = character.copy(location = newLocation)
        CharacterCache.put(character)
        saveCharacter(character)

        showTownMenu(character)
    }

}