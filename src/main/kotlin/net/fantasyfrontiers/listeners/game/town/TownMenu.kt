package net.fantasyfrontiers.listeners.game.town

import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.model.player.Character
import net.fantasyfrontiers.utils.functions.checkIfAlreadyTraveling
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.FileUpload
import net.fantasyfrontiers.data.model.town.Location
import net.fantasyfrontiers.data.model.town.LocationActions

fun IReplyCallback.showTownMenu(character: Character, extraInformation: String? = null) {
    val languageCode = character.language

    if (checkIfAlreadyTraveling(this, languageCode)) return

    val location = character.location
    val town = location.town
    val locationImage = location.locationImage.toFile()

    val embed = EmbedBuilder()
        .setTitle(TranslationCache.get(languageCode, "town.menu.title.${location.specialLocation.name}", mapOf("town" to town.name)).toString())
        .setDescription(
            "*" + TranslationCache.get(languageCode, "town.description.${town.name.replace(" ", "")}").toString() + "*"
                    + "\n\n" + (if(extraInformation != null) extraInformation + "\n\n" else "") +
            TranslationCache.get(languageCode, "town.menu.description").toString()
        )
        .setImage("attachment://${town.name.replace(" ", "")}.png")
        .setColor(0x57F287)
        .build()

    val buttons = location.generateTravelButtons(character)
    buttons.add(
        Button.secondary("ff-town-menu-travel", TranslationCache.get(languageCode, "town.menu.travel").toString())
            .withEmoji(Emoji.fromFormatted("<:cart:1164528666483621903>"))
    )

    // An action Row can only hold up to 5 buttons, so we need to split them up into multiple rows.
    val actionRows = mutableListOf<ActionRow>()
    var rows = 0
    while (buttons.isNotEmpty()) {
        if(rows == 4) break
        actionRows.add(ActionRow.of(buttons.take(5)))
        buttons.removeAll(buttons.take(5))
        rows++
    }


    if (isAcknowledged) {
        hook.editOriginalEmbeds(embed).setFiles(
            FileUpload.fromData(locationImage, "${town.name.replace(" ", "")}.png")
        ).setComponents(actionRows).queue()
    } else {
        replyEmbeds(
            embed
        ).addFiles(
            FileUpload.fromData(locationImage, "${town.name.replace(" ", "")}.png")
        ).addComponents(
            actionRows
        ).setEphemeral(true).queue()
    }
}

fun Location.generateTravelButtons(character: Character): MutableList<Button> {
    val travelLocations = travelLocations()

    val buttons = mutableListOf<Button>()

    for (action in LocationActions.getActions(specialLocation)) {
        buttons.add(
            Button.secondary("ff-town-menu-action-${action.name}", TranslationCache.get(character.language, "town.menu.action.${action.name}").toString())
                .withEmoji(Emoji.fromFormatted(action.emoji)).withDisabled(!action.testEnabled(character, specialLocation))
        )
    }

    for (travelLocation in travelLocations) {
        buttons.add(
            Button.secondary("ff-town-menu-walk-${travelLocation.name}", TranslationCache.get(character.language, "town.menu.walk.${travelLocation.name}").toString())
                .withEmoji(Emoji.fromFormatted(travelLocation.emoji))
        )
    }

    return buttons
}