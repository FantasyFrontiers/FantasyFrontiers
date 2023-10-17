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
        stats, _ -> stats.copy(luck = stats.luck + 10)
    }

    /**
     * List of skills.
     */
    private val skills = listOf(
        MEGALUCK,
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