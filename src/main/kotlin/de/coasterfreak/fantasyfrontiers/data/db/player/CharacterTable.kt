package de.coasterfreak.fantasyfrontiers.data.db.player

import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.model.player.Character
import de.coasterfreak.fantasyfrontiers.data.model.player.NobleTitle
import de.coasterfreak.fantasyfrontiers.data.model.player.Stats
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This class represents a character table in a database.
 *
 * The character table contains the following columns:
 * - discord_client_id: The ID of the character's Discord client.
 * - first_name: The first name of the character.
 * - last_name: The last name of the character.
 * - noble_title: The noble title of the character (nullable).
 * - money: The amount of money the character has (default: 0).
 * - location: The current location of the character.
 *
 * Stats:
 * - health_points: The health points of the character (default: 20).
 * - mana_points: The mana points of the character (default: 20).
 * - strength: The strength stat of the character (default: 1).
 * - vitality: The vitality stat of the character (default: 1).
 * - dexterity: The dexterity stat of the character (default: 1).
 * - agility: The agility stat of the character (default: 1).
 * - intelligence: The intelligence stat of the character (default: 1).
 * - magic: The magic stat of the character (default: 1).
 * - charisma: The charisma stat of the character (default: 1).
 * - reputation: The reputation stat of the character (default: 0).
 * - luck: The luck stat of the character (default: 1).
 *
 * The primary key of the table is the discord_client_id column.
 */
object CharacterTable: Table("characters") {
    val discordClientID = varchar("discord_client_id", 24)
    val language = varchar("language", 5).default("en-US")

    val firstName = varchar("first_name", 32)
    val lastName = varchar("last_name", 32)
    val nobleTitle = enumeration("noble_title", NobleTitle::class).nullable()
    val money = long("money").default(0)
    val location = varchar("location", 50).default("MistMeadow")

    // Stats
    val healthPoints = integer("health_points").default(20)
    val manaPoints = integer("mana_points").default(20)
    val strength = integer("strength").default(1)
    val vitality = integer("vitality").default(1)
    val dexterity = integer("dexterity").default(1)
    val agility = integer("agility").default(1)
    val intelligence = integer("intelligence").default(1)
    val magic = integer("magic").default(1)
    val charisma = integer("charisma").default(1)
    val reputation = integer("reputation").default(0)
    val luck = integer("luck").default(1)

    override val primaryKey = PrimaryKey(discordClientID)
}

/**
 * Loads a character based on the Discord client ID.
 *
 * This method retrieves the character information from the database based on the provided Discord client ID.
 * It maps the database row to a `Character` instance using the retrieved values.
 * The method also retrieves the character's skills and guild ranks using the `getAllSkills` and `getAllGuilds` methods, respectively.
 * If a character with the specified Discord client ID is found in the database, the method returns the corresponding `Character` instance.
 * If no character is found, it returns `null`.
 *
 * @param discordClientID The Discord client ID of the character to load.
 * @return The `Character` instance representing the loaded character, or `null` if no character is found.
 */
fun loadCharacter(discordClientID: String) = transaction {
    CharacterTable.select { CharacterTable.discordClientID eq discordClientID }.map { row ->
        Character(
            discordClientID = row[CharacterTable.discordClientID],
            language = row[CharacterTable.language],
            firstName = row[CharacterTable.firstName],
            lastName = row[CharacterTable.lastName],
            nobleTitle = row[CharacterTable.nobleTitle],
            money = row[CharacterTable.money],
            location = TownCache.get(row[CharacterTable.location]),
            stats = Stats(
                healthPoints = row[CharacterTable.healthPoints],
                manaPoints = row[CharacterTable.manaPoints],
                strength = row[CharacterTable.strength],
                vitality = row[CharacterTable.vitality],
                dexterity = row[CharacterTable.dexterity],
                agility = row[CharacterTable.agility],
                intelligence = row[CharacterTable.intelligence],
                magic = row[CharacterTable.magic],
                charisma = row[CharacterTable.charisma],
                reputation = row[CharacterTable.reputation],
                luck = row[CharacterTable.luck],
            ),
            skills = getAllSkills(discordClientID),
            guildRanks = getAllGuilds(discordClientID),
        )
    }.firstOrNull()
}


/**
 * Saves the character information to the database.
 *
 * This method updates the character's information, including their Discord client ID, language, name, noble title, money, location, and stats.
 * It also updates the character's skills and guild ranks in separate database tables.
 *
 * @param character The character to save.
 */
fun saveCharacter(character: Character) = transaction {
    CharacterTable.replace {
        it[discordClientID] = character.discordClientID
        it[language] = character.language
        it[firstName] = character.firstName
        it[lastName] = character.lastName
        it[nobleTitle] = character.nobleTitle
        it[money] = character.money
        it[location] = character.location.name

        // Stats
        it[healthPoints] = character.stats.healthPoints
        it[manaPoints] = character.stats.manaPoints
        it[strength] = character.stats.strength
        it[vitality] = character.stats.vitality
        it[dexterity] = character.stats.dexterity
        it[agility] = character.stats.agility
        it[intelligence] = character.stats.intelligence
        it[magic] = character.stats.magic
        it[charisma] = character.stats.charisma
        it[reputation] = character.stats.reputation
        it[luck] = character.stats.luck
    }

    updateAllSkills(character.discordClientID, character.skills)
    updateAllGuilds(character.discordClientID, character.guildRanks)
    return@transaction
}