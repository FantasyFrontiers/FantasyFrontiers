package de.coasterfreak.fantasyfrontiers.data.db.player

import de.coasterfreak.fantasyfrontiers.data.model.player.Skill
import de.coasterfreak.fantasyfrontiers.data.model.player.Skills
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents a table in the database that stores character skills.
 *
 * This table is used to map character skills to their respective Discord client ID, skill name, and experience.
 * Each skill is uniquely identified by the combination of the Discord client ID and skill name.
 *
 * @property discordClientID The column that stores the Discord client ID of the character.
 * @property skillName The column that stores the name of the skill.
 * @property experience The column that stores the experience value of the skill.
 * @property primaryKey The primary key of the table, which consists of the discordClientID and skillName columns.
 *
 * @constructor Creates an instance of the CharacterSkillTable object.
 */
object CharacterSkillTable: Table("character_skills") {
    val discordClientID = varchar("discord_client_id", 24).references(CharacterTable.discordClientID, onDelete = ReferenceOption.CASCADE)
    val skillName = varchar("skill_name", 32)
    val experience = long("experience")

    override val primaryKey = PrimaryKey(discordClientID, skillName)
}

/**
 * Retrieves all the skills and their corresponding experience for a given Discord client ID.
 *
 * @param discordClientID The Discord client ID of the user.
 * @return A map containing the skills as keys and their corresponding experience as values.
 */
fun getAllSkills(discordClientID: String): Map<Skill, Long> = transaction {
    return@transaction CharacterSkillTable.select { CharacterSkillTable.discordClientID eq discordClientID }
        .associate { row ->
            Skills.getSkill(row[CharacterSkillTable.skillName]) to row[CharacterSkillTable.experience]
        }
}

/**
 * Updates the skills of a character in the database.
 *
 * @param discordClientID The ID of the Discord client.
 * @param skills The map of skills and their corresponding experience values to update.
 */
fun updateAllSkills(discordClientID: String, skills: Map<Skill, Long>) = transaction {
    CharacterSkillTable.batchReplace(skills.entries) { (skill, experience) ->
        this[CharacterSkillTable.discordClientID] = discordClientID
        this[CharacterSkillTable.skillName] = skill.name
        this[CharacterSkillTable.experience] = experience
    }
}