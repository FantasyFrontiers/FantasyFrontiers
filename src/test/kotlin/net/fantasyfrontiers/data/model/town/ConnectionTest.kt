package net.fantasyfrontiers.data.model.town

import net.fantasyfrontiers.data.model.player.Character
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ConnectionTest {

    private var fakeCharacter = Character(
        "---",
        "en-US",
        "Test",
        "Test",
        location = Location(Town("Test1"), SpecialLocation.OUTSIDE)
    )

    @Test
    fun getTravelDuration() {
        val connection = Connection("Test2", 12)
        val connection2 = Connection("Test3", 1000)

        assertEquals(2, connection.getTravelDuration(fakeCharacter).inWholeSeconds)
        assertEquals(166, connection2.getTravelDuration(fakeCharacter).inWholeSeconds)

        fakeCharacter = fakeCharacter.copy(
            stats = fakeCharacter.stats.copy(
                agility = 100
            )
        )

        assertEquals(62, connection2.getTravelDuration(fakeCharacter).inWholeSeconds)
    }
}