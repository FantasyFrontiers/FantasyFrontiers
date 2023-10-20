package net.fantasyfrontiers.utils.functions

import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.db.player.saveCharacter
import net.fantasyfrontiers.data.model.player.Character
import net.fantasyfrontiers.data.model.player.Skill

/**
 * Creates a new character with the provided information and saves it to the database.
 *
 * @param discordClientID The ID of the Discord client associated with the character.
 * @param languageCode The language code of the character's preferred language.
 * @param firstName The first name of the character.
 * @param lastName The last name of the character.
 * @param skillSet The list of skills for the character. Defaults to an empty list.
 * @return The newly created character.
 */
fun createNewCharacter(
    discordClientID: String,
    languageCode: String,
    firstName: String,
    lastName: String,
    skillSet: List<Skill> = emptyList(),
): Character {
    val newCharacter = Character(discordClientID, languageCode, firstName, lastName, skills = skillSet.associateWith { 0 })

    saveCharacter(newCharacter)
    CharacterCache.put(newCharacter, true)

    return newCharacter
}