package de.coasterfreak.fantasyfrontiers.data.cache

import de.coasterfreak.fantasyfrontiers.data.db.discord.loadServerSettings
import de.coasterfreak.fantasyfrontiers.data.model.discord.ServerSettings
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object ServerSettingsCache {

    /**
     * This variable represents a ReadWriteLock object named 'lock'. It is used for managing concurrent access to shared resources.
     * The ReadWriteLock allows multiple threads to read the resource concurrently, but only one thread can write to the resource at a time.
     */
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    /**
     * Represents a cache of server settings.
     *
     * This cache stores server settings with their guild IDs as keys and `ServerSettings` objects as values.
     * It is implemented as a mutable map.
     *
     * @property cache The mutable map that stores server settings.
     */
    private var cache: Map<String, ServerSettings> = emptyMap()


    /**
     * Retrieves the server settings for a given guild ID.
     *
     * @param guildId The ID of the guild for which to retrieve the settings.
     * @return The server settings for the specified guild ID. If no settings are found, a default ServerSettings object
     *         with the specified guild ID is returned.
     */
    fun get(guildId: String): ServerSettings {
        try {
            lock.readLock().lock()
            val serverSettings = cache[guildId] ?: loadServerSettings(guildId)
            cache = cache + (guildId to serverSettings)
            return serverSettings
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Inserts or updates the server settings for a guild.
     *
     * @param guildId The ID of the guild for which to update the settings.
     * @param serverSettings The settings to be updated.
     * @return The updated server settings.
     */
    fun put(guildId: String, serverSettings: ServerSettings): ServerSettings {
        try {
            lock.writeLock().lock()
            cache = cache + (guildId to serverSettings)
            return serverSettings
        } finally {
            lock.writeLock().unlock()
        }
    }
}