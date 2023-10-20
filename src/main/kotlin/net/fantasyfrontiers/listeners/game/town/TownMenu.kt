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

fun IReplyCallback.showTownMenu(character: Character) {
    val languageCode = character.language

    if (checkIfAlreadyTraveling(this, languageCode)) return

    val town = character.location
    val townMapImage = town.townMapImage.toFile()

    val embed = EmbedBuilder()
        .setTitle(TranslationCache.get(languageCode, "town.menu.title", mapOf("town" to town.name)).toString())
        .setDescription(
            "*" + TranslationCache.get(languageCode, "town.description.${town.name.replace(" ", "")}").toString() + "*"
                    + "\n\n" +
            TranslationCache.get(languageCode, "town.menu.description").toString()
        )
        .setImage("attachment://${town.name.replace(" ", "")}.png")
        .setColor(0x57F287)
        .build()

    val buttons = mutableListOf(
        Button.secondary("ff-town-menu-travel", TranslationCache.get(languageCode, "town.menu.travel").toString())
            .withEmoji(Emoji.fromFormatted("<:cart:1164528666483621903>")),
    )

    if (town.features.port) {
        buttons.add(0,
            Button.secondary("ff-town-menu-port", TranslationCache.get(languageCode, "town.menu.port").toString())
                .withEmoji(Emoji.fromFormatted("⚓"))
        )
    }

    if (town.features.plaza) {
        buttons.add(0,
            Button.secondary("ff-town-menu-plaza", TranslationCache.get(languageCode, "town.menu.plaza").toString())
                .withEmoji(Emoji.fromFormatted("<:market:1164514485814444042>"))
        )
    }

    if (town.features.temple) {
        buttons.add(0,
            Button.secondary("ff-town-menu-church", TranslationCache.get(languageCode, "town.menu.church").toString())
                .withEmoji(Emoji.fromFormatted("⛪"))
        )
    }

    // An action Row can only hold up to 5 buttons, so we need to split them up into multiple rows.
    val actionRows = mutableListOf<ActionRow>()
    var rows = 0
    while (buttons.isNotEmpty()) {
        if(rows == 4) break
        actionRows.add(ActionRow.of(buttons.take(5)))
        buttons.removeAll(buttons.take(5))
        rows++
    }


    replyEmbeds(
        embed
    ).addFiles(
        FileUpload.fromData(townMapImage, "${town.name}.png")
    ).addComponents(
        actionRows
    ).setEphemeral(true).queue()
}