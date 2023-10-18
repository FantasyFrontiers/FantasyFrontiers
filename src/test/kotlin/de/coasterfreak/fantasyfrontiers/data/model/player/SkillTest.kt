package de.coasterfreak.fantasyfrontiers.data.model.player

import de.coasterfreak.fantasyfrontiers.utils.extensions.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

/**
 * Represents a class for testing skill functionality.
 */
internal class SkillTest {

    /**
     * Converts the given level to experience based on the experience ratio.
     *
     * @param level The level to convert to experience.
     * @return The experience corresponding to the given level.
     */
    @Test
    fun experienceToLevelAndBack() {
        val skill = Skills.MANA_CONTROL
        for (i in 1..skill.maxLevel) {
            val exp = skill.levelToExperience(i)
            val level = skill.experienceToLevel(exp)
            assertEquals(i, level)
        }
    }

    /**
     * Handles the calculation and verification of experience-to-level conversion for other experience ratios.
     *
     * This method is responsible for testing the functionality of converting experience to level for skills with different
     * experience ratios. It generates random experience ratios and tests the conversion for each level of the skill.
     *
     * @see SkillTest.experienceToLevelAndBack
     * @see Skill.levelToExperience
     * @see Skill.experienceToLevel
     */
    @Test
    fun handleOtherExperienceRatios() {
        for (i in 1..10) {
            val ratio = Random.nextDouble(0.5, 20.0).round(2)
            val skill = Skill("test", 20, ratio) { stats, exp -> stats }
            for (j in 1..skill.maxLevel) {
                val exp = skill.levelToExperience(j)
                val level = skill.experienceToLevel(exp)
                assertEquals(j, level)
            }
        }
    }


    /**
     * Modifies the stats of a character based on the skill's effect.
     *
     * The modifyStats method takes the current stats of a character and the experience gained in a skill as input parameters.
     * It returns the modified stats after applying the skill's effect on the character's stats. The method uses the experienceToLevel
     * method to convert the experience to the corresponding skill level, and then modifies specific stats based on the level.
     *
     * @param stats The current stats of the character.
     * @param exp The experience gained in the skill.
     * @return The modified stats after applying the skill's effect.
     */
    @Test
    fun modifyStats() {
        val skill = Skill("test", 20, 1.5) { stats, exp ->
            stats.copy(
                manaPoints = stats.manaPoints + (5 * this.experienceToLevel(exp)),
                intelligence = stats.intelligence + (3 * this.experienceToLevel(exp)),
                magic = stats.magic + (this.experienceToLevel(exp))
            )
        }
        val stats = Stats()
        val exp = skill.levelToExperience(5)
        val modifiedStats = skill.modifyStats.invoke(skill, stats, exp)
        assertEquals(45, modifiedStats.manaPoints)
        assertEquals(16, modifiedStats.intelligence)
        assertEquals(6, modifiedStats.magic)
    }
}