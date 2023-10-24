package net.fantasyfrontiers.data.db.player

import net.fantasyfrontiers.data.model.guild.GuildCard
import net.fantasyfrontiers.data.model.guild.Guilds
import net.fantasyfrontiers.data.model.player.Skill
import net.fantasyfrontiers.data.model.player.Skills
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A table representing the character guilds in the game.
 *
 * This table is used to store information about character's guild membership, including
 * the Discord client ID, guild name, and experience.
 */
object CharacterGuildsTable: Table("character_guilds") {
    val discordClientID = varchar("discord_client_id", 24).references(CharacterTable.discordClientID, onDelete = ReferenceOption.CASCADE)
    val guildName = enumeration("guild_name", Guilds::class)
    val experience = long("experience")

    override val primaryKey = PrimaryKey(discordClientID, guildName)
}

/**
 * Retrieves all guilds associated with the given Discord client ID.
 *
 * @param discordClientID The ID of the Discord client.
 * @return A map of guilds and their corresponding experience values. The keys are of type [Guilds], which represents the available guilds in the game, and the values are of type [Long] representing the experience values.
 */
fun getAllGuilds(discordClientID: String): List<GuildCard> = transaction {
    return@transaction CharacterGuildsTable.select { CharacterGuildsTable.discordClientID eq discordClientID }
        .map { row ->
            GuildCard(row[CharacterGuildsTable.guildName], row[CharacterGuildsTable.experience])
        }
}


/**
 * Updates the guild information for all guilds in the game.
 *
 * @param discordClientID The Discord client ID associated with the guilds.
 * @param guilds A map containing the guild names and their corresponding experience values.
 */
fun updateAllGuilds(discordClientID: String, guilds: List<GuildCard>) = transaction {
    CharacterGuildsTable.batchReplace(guilds) { (guild, experience) ->
        this[CharacterGuildsTable.discordClientID] = discordClientID
        this[CharacterGuildsTable.guildName] = guild
        this[CharacterGuildsTable.experience] = experience
    }
}