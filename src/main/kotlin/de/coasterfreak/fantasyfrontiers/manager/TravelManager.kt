package de.coasterfreak.fantasyfrontiers.manager

import de.coasterfreak.fantasyfrontiers.data.cache.CharacterCache
import de.coasterfreak.fantasyfrontiers.data.cache.TownCache
import de.coasterfreak.fantasyfrontiers.data.db.player.saveCharacter
import de.coasterfreak.fantasyfrontiers.data.model.town.Travel
import de.coasterfreak.fantasyfrontiers.utils.functions.withTestPermission
import java.util.concurrent.locks.ReentrantReadWriteLock

object TravelManager {

    private var travelQueue = listOf<Travel>()
    private val readWriteLock = ReentrantReadWriteLock()
    private var travelThread: Thread? = null

    fun add(travel: Travel) {
        try {
            readWriteLock.writeLock().lock()
            travelQueue = travelQueue + travel
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    fun remove(travel: Travel) {
        try {
            readWriteLock.writeLock().lock()
            travelQueue = travelQueue - travel
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    fun get(discordClientId: String): Travel? {
        try {
            readWriteLock.readLock().lock()
            return travelQueue.find { it.character.discordClientID == discordClientId }
        } finally {
            readWriteLock.readLock().unlock()
        }
    }

    fun getAll(): List<Travel> {
        try {
            readWriteLock.readLock().lock()
            return travelQueue
        } finally {
            readWriteLock.readLock().unlock()
        }
    }

    /**
     * Starts a new thread that continuously checks the travel queue for completed journeys.
     * If a journey's end time has passed, a message is sent to the corresponding threadChannel
     * indicating that the journey has been completed. The threadChannel is then deleted, and
     * the journey is removed from the travel queue.
     *
     * @throws NullPointerException if travelThread is null
     *
     * @see Journey
     */
    fun startThread() {
        travelThread = Thread {
            while (true) {
                try {
                    readWriteLock.readLock().lock()
                    travelQueue.forEach { travel ->
                        if (travel.end.timeInMilliseconds <= System.currentTimeMillis()) {
                            val updatedCharacter = travel.character.copy(location = TownCache.get(travel.connection.name))
                            CharacterCache.put(updatedCharacter)
                            saveCharacter(updatedCharacter)

                            travel.threadChannel.sendMessage("You have arrived at ${travel.connection.name}!").queue {
                                Thread {
                                    remove(travel)
                                    Thread.sleep(10000)
                                    travel.threadChannel.withTestPermission {
                                        this.delete().queue()
                                    }
                                }.start()
                            }
                            return@forEach
                        }
                        travel.testForRandomEncounter()
                    }
                } finally {
                    readWriteLock.readLock().unlock()
                }
                Thread.sleep(1000)
            }
        }
        travelThread!!.start()
    }

    /**
     * Stops the thread.
     *
     * This method interrupts the `travelThread` if it is running or has been started.
     *
     * Note: The behavior of a thread after it has been interrupted is dependent on how the thread is implemented.
     * It is recommended to check the interrupted status and handle it appropriately in the implementation of the thread.
     */
    fun stopThread() {
        travelQueue.forEach { travel ->
            travel.threadChannel.delete().queue()
        }

        travelThread?.interrupt()
    }
}