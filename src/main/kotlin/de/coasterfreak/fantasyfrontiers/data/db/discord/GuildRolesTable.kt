package de.coasterfreak.fantasyfrontiers.data.db.discord

import de.coasterfreak.fantasyfrontiers.data.model.discord.GuildRole
import de.coasterfreak.fantasyfrontiers.data.model.guild.Guilds
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents the database table for storing guild roles.
 *
 * @property guildID The column representing the guild ID.
 * @property ingameGuild The column representing the in-game guild.
 * @property roleID The column representing the role ID.
 */
object GuildRolesTable : Table("guild_roles") {
    val guildID = varchar("guild_id", 24).references(ServerSettingsTable.guildID, onDelete = ReferenceOption.CASCADE)

    val ingameGuild = enumeration("ingame_guild", Guilds::class)
    val roleID = varchar("role_id", 24)

    override val primaryKey = PrimaryKey(guildID, ingameGuild)
}

/**
 * Returns the list of guild roles associated with the specified guild ID.
 *
 * @param guildId The ID of the guild.
 * @return The list of guild roles.
 */
fun getGuildRoles(guildId: String): List<GuildRole> = transaction {
    return@transaction GuildRolesTable.select { GuildRolesTable.guildID eq guildId }.map { row ->
        GuildRole(
            guild = row[GuildRolesTable.ingameGuild],
            roleId = row[GuildRolesTable.roleID]
        )
    }
}

/**
 * Updates the guild roles for a specific guild.
 *
 * @param guildId The ID of the guild whose roles are being updated.
 * @param guildRoles The list of guild roles to be updated.
 */
fun updateGuildRoles(guildId: String, guildRoles: List<GuildRole>) = transaction {
    GuildRolesTable.batchReplace(guildRoles) {
        this[GuildRolesTable.guildID] = guildId
        this[GuildRolesTable.ingameGuild] = it.guild
        this[GuildRolesTable.roleID] = it.roleId
    }
}