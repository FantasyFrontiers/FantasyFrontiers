package net.fantasyfrontiers.data.cache

import net.fantasyfrontiers.data.db.player.CharacterTable
import net.fantasyfrontiers.data.db.player.loadCharacter
import net.fantasyfrontiers.data.model.player.Character
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.fantasyfrontiers.FantasyFrontiers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.properties.Delegates

object CharacterCache {


    /**
     * Represents a lock used for controlling access to a resource.
     *
     * This lock provides a read-write locking mechanism, allowing multiple readers to access the resource concurrently
     * but only allowing a single writer to modify the resource at a time. This helps to prevent data corruption and inconsistency
     * when multiple threads or processes are accessing the resource simultaneously.
     *
     * This lock is implemented using the [ReadWriteLock] interface and the [ReentrantReadWriteLock] class.
     *
     * @see ReadWriteLock
     * @see ReentrantReadWriteLock
     */
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    /**
     * Represents a cache storing character information.
     *
     * This cache is used to store character objects, with the Discord client ID as the key and the character object as the value.
     * It provides methods for retrieving and adding character objects to the cache.
     *
     * @property cache The map representing the cache, with the Discord client ID as the key and the character object as the value.
     */
    private var cache: Map<String, Character> = emptyMap()

    /**
     * Represents the total number of characters created.
     */
    var totalCharacters: Long by Delegates.observable(0L) { _, _, newValue ->
        getItsLogger().info("$newValue characters in localStorage.")

        FantasyFrontiers.instance.refreshStatus()
    }
        private set

    fun loadStatistics() = transaction {
        totalCharacters = CharacterTable.selectAll().count()
        getItsLogger().info("$totalCharacters characters in database.")
        return@transaction
    }


    /**
     * Retrieves a character based on the Discord client ID.
     *
     * This method retrieves a character from the cache based on the provided Discord client ID.
     * If the character is not found in the cache, it is loaded from the database using the [loadCharacter] method.
     * The loaded character is then added to the cache.
     *
     * @param discordClientID The Discord client ID of the character to retrieve.
     * @return The Character instance representing the retrieved character, or null if the character is not found.
     */
    fun get(discordClientID: String): Character? {
        try {
            lock.readLock().lock()
            val character = cache[discordClientID] ?: loadCharacter(discordClientID)
            if (character != null) {
                cache = cache + (discordClientID to character)
            }
            return character
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Adds or updates a character in the cache.
     *
     * This method adds or updates a character in the cache. The character is associated with a unique Discord client ID.
     * If the character already exists in the cache, it will be replaced with the new character.
     *
     * @param character The character to be added or updated in the cache. The character must have a valid Discord client ID.
     * @return The added or updated character.
     */
    fun put(character: Character, created: Boolean = false): Character {
        try {
            lock.writeLock().lock()
            cache = cache + (character.discordClientID to character)
            if (created) {
                totalCharacters++
            }
            return character
        } finally {
            lock.writeLock().unlock()
        }
    }
}