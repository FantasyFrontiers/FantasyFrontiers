package de.coasterfreak.fantasyfrontiers.data.cache

import de.coasterfreak.fantasyfrontiers.data.db.getAllTowns
import de.coasterfreak.fantasyfrontiers.data.model.town.Town
import dev.fruxz.ascend.extension.logging.getItsLogger
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.time.measureTime

/**
 * The TownCache class is responsible for storing and managing a cache of Town objects.
 * It provides methods for retrieving, adding, removing, and clearing towns in the cache.
 *
 * This class uses a ReadWriteLock to ensure thread-safety when accessing the cache.
 * Multiple threads can read from the cache simultaneously, but only one thread can write to the cache at a time.
 *
 * Usage:
 *  - To retrieve all towns in the cache, use the getAll() method.
 *  - To retrieve a specific town by name, use the get(name: String) method.
 *  - To add a new town to the cache, use the put(name: String, town: Town) method.
 *  - To remove a town from the cache by name, use the remove(name: String) method.
 *  - To clear all towns from the cache, use the clear() method.
 */
object TownCache {

    /**
     * This variable represents a ReadWriteLock object named 'lock'. It is used for managing concurrent access to shared resources.
     * The ReadWriteLock allows multiple threads to read the resource concurrently, but only one thread can write to the resource at a time.
     */
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    /**
     * Represents a cache of towns.
     *
     * This cache stores towns with their names as keys and `Town` objects as values.
     * It is implemented as a mutable map.
     *
     * @property cache The mutable map that stores towns.
     */
    private var cache: Map<String, Town> = emptyMap()

    /**
     * Loads all towns from the database and stores them in the cache.
     */
    fun loadAll() {
        val loadTimer = measureTime {
            val towns = getAllTowns()
            towns.forEach {
                put(it.name, it)
            }
        }
        getItsLogger().info("Loaded ${cache.size} towns in $loadTimer.")
    }

    /**
     * Returns a list of all towns in the cache.
     *
     * @return The list of towns in the cache. An empty list is returned if the cache is empty.
     */
    fun getAll(): List<Town> {
        try {
            lock.readLock().lock()
            return cache.values.toList()
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Retrieves the `Town` object with the specified name from the cache.
     *
     * @param name The name of the town to retrieve.
     *
     * @return The `Town` object with the specified name, or `null` if the town is not found in the cache.
     */
    fun getOrNull(name: String): Town? {
        try {
            lock.readLock().lock()
            return cache[name]
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Retrieves a town by its name from the cache.
     *
     * @param name The name of the town to retrieve.
     * @return The town with the specified name.
     * @throws IllegalArgumentException If a town with the specified name is not found in the cache.
     */
    fun get(name: String): Town {
        try {
            lock.readLock().lock()
            return cache[name] ?: throw IllegalArgumentException("Town with name '$name' not found.")
        } finally {
            lock.readLock().unlock()
        }
    }

    /**
     * Inserts or updates the town with the given name in the cache.
     *
     * @param name The name of the town to be inserted or updated.
     * @param town The Town object representing the details of the town.
     */
    fun put(name: String, town: Town) {
        try {
            lock.writeLock().lock()
            cache = cache + (name to town)
        } finally {
            lock.writeLock().unlock()
        }
    }

    /**
     * Removes the value associated with the given name from the cache.
     *
     * @param name The name of the value to remove from the cache.
     */
    fun remove(name: String) {
        try {
            lock.writeLock().lock()
            cache = cache - name
        } finally {
            lock.writeLock().unlock()
        }
    }

    /**
     * Clears the cache.
     *
     * This method acquires a write lock to ensure exclusive access to the cache and then clears it.
     * After the cache has been cleared, the write lock is released.
     *
     * Note: It is recommended to always call this method within a try-finally block to ensure that the write lock is always released, even in case of an exception.
     */
    fun clear() {
        try {
            lock.writeLock().lock()
            cache = emptyMap()
        } finally {
            lock.writeLock().unlock()
        }
    }

}