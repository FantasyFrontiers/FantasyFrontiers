package de.coasterfreak.fantasyfrontiers.data.model.player

object Skills {


    /**
     * A skill that enhances luck.
     *
     * @property name The name of the skill.
     * @property maxLevel The maximum level of the skill.
     * @property experienceRatio The experience ratio of the skill.
     * @property modifyStats A lambda function that modifies the stats of a character.
     */
    val MEGALUCK = Skill("MEGALUCK", 1) {
        stats, _ -> stats.copy(luck = stats.luck + 20)
    }

    /**
     * Represents a skill called MANA_CONTROL.
     *
     * This skill increases the character's mana points, intelligence, and magic based on the experience gained.
     * The stats are modified using the `modifyStats` function provided by the `Skill` class.
     *
     * @property MANA_CONTROL The instance of the skill.
     *
     * @see Skill
     * @see Skill.modifyStats
     * @see Stats
     */
    val MANA_CONTROL = Skill("MANA_CONTROL", 20) {
        stats, exp -> stats.copy(
            manaPoints = stats.manaPoints + (5 * this.experienceToLevel(exp)),
            intelligence = stats.intelligence + (3 * this.experienceToLevel(exp)),
            magic = stats.magic + (this.experienceToLevel(exp))
        )
    }

    /**
     * ANCIENT_FIGHTING_SKILLS
     *
     * Represents a skill that enhances ancient fighting skills. When this skill is applied to a character's stats, it increases the strength, vitality, dexterity, and agility attributes.
     *
     * @property name The name of the skill.
     * @property maxLevel The maximum level of the skill.
     * @property experienceRatio The experience ratio of the skill.
     * @property modifyStats The function that modifies a character's stats when this skill is applied.
     */
    val ANCIENT_FIGHTING_SKILLS = Skill("ANCIENT_FIGHTING_SKILLS", 1) {
        stats, _ -> stats.copy(
            strength = stats.strength + 8,
            vitality = stats.vitality + 5,
            dexterity = stats.dexterity + 3,
            agility = stats.agility + 4
        )
    }



    /**
     * List of skills.
     */
    private val skills = listOf(
        MEGALUCK,
        MANA_CONTROL,
        ANCIENT_FIGHTING_SKILLS
    )

    /**
     * The STARTER_SKILL_SET variable represents a list of starter skills for a character.
     * A Character can only choose one of these skills.
     * The skill will be added to the character's skill set.
     * Most of these skills cannot be learned later on.
     *
     * @property STARTER_SKILL_SET The list of starter skills.
     */
    val STARTER_SKILL_SET = mapOf(
        MEGALUCK to 1,
        MANA_CONTROL to 3,
        ANCIENT_FIGHTING_SKILLS to 1
    )

    /**
     * Retrieves the skill with the specified name.
     *
     * @param name The name of the skill to retrieve.
     * @return The skill with the specified name.
     * @throws IllegalArgumentException if no skill with the specified name is found.
     */
    fun getSkill(name: String): Skill = skills.find { it.name == name } ?: throw IllegalArgumentException("No skill with name $name found.")
}