package de.coasterfreak.fantasyfrontiers.manager

import de.coasterfreak.fantasyfrontiers.annotations.MessageCommand
import de.coasterfreak.fantasyfrontiers.annotations.SlashCommand
import de.coasterfreak.fantasyfrontiers.annotations.UserCommand
import de.coasterfreak.fantasyfrontiers.interfaces.HasOptions
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommandGroups
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommands
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.reflections8.Reflections
import kotlin.time.measureTime

/**
 * The RegisterManager class is responsible for registering listeners and commands for a JDA instance.
 * It maintains a map of loaded classes to avoid duplicate registrations.
 *
 * The main methods in this class are `registerAll()` and `registerCommands()`.
 *
 * Example usage:
 * ```
 * val jdaBuilder = JDABuilder.createDefault(token)
 * jdaBuilder.registerAll()
 *       .registerCommands()
 *       .build()
 * ```
 */
object RegisterManager {

    /**
     * Stores a map of loaded classes.
     *
     * The keys represent the simple names of the loaded classes,
     * and the values represent instances of the loaded classes.
     */
    private var loadedClasses = mapOf<String, Any>()

    /**
     * Registers all ListenerAdapters and EventListeners in the given package.
     *
     * @return The JDABuilder object.
     */
    fun JDABuilder.registerAll() : JDABuilder {
        val reflections = Reflections("de.coasterfreak.fantasyfrontiers")

        // Registering both ListenerAdapters and EventListeners
        val listenerTime = measureTime {
            for (clazz in (reflections.getSubTypesOf(ListenerAdapter::class.java) + reflections.getSubTypesOf(EventListener::class.java)).distinct()) {
                if (clazz.simpleName == "ListenerAdapter") continue

                val constructor = clazz.getDeclaredConstructor()
                constructor.trySetAccessible()

                val listener = constructor.newInstance()
                loadedClasses += clazz.simpleName to listener
                addEventListeners(listener)
                getItsLogger().info("Registered listener: ${listener.javaClass.simpleName}")
            }
        }
        getItsLogger().info("Registered listeners in $listenerTime")

        return this
    }

    /**
     * Registers the commands defined by annotations in the provided JDA instance.
     * Uses reflection to scan for classes annotated with `SlashCommand`, `UserCommand`, and `MessageCommand`.
     * Uses the provided JDA instance to register the commands using the `upsertCommand()` method.
     * Adds the registered command classes to a `loadedClasses` map for future reference.
     * Returns the modified JDA instance.
     *
     * @return The modified JDA instance after registering the commands.
     */
    fun JDA.registerCommands(): JDA {
        val reflections = Reflections("de.coasterfreak.fantasyfrontiers")

        // Registering commands
        val commandsTime = measureTime {
            for (clazz in reflections.getTypesAnnotatedWith(SlashCommand::class.java)) {
                val annotation = clazz.getAnnotation(SlashCommand::class.java)
                val data = Commands.slash(annotation.name, annotation.description)

                if (clazz.simpleName !in loadedClasses) {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.trySetAccessible()

                    val command = constructor.newInstance()
                    loadedClasses += clazz.simpleName to command
                    getItsLogger().info("Registered command class: ${command.javaClass.simpleName}")
                }

                val command = loadedClasses[clazz.simpleName]

                if (command is HasOptions) {
                    data.addOptions(command.getOptions())
                }

                if (command is HasSubcommandGroups) {
                    data.addSubcommandGroups(command.getChoices())
                }

                if (command is HasSubcommands) {
                    data.addSubcommands(command.getSubCommands())
                }


                upsertCommand(data).queue()
                getItsLogger().info("Registered global command: ${annotation.name}")
            }

            // UserCommands
            for (clazz in reflections.getTypesAnnotatedWith(UserCommand::class.java)) {
                val annotation = clazz.getAnnotation(UserCommand::class.java)
                val data = Commands.user(annotation.name)

                if (clazz.simpleName !in loadedClasses) {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.trySetAccessible()

                    val command = constructor.newInstance()
                    loadedClasses += clazz.simpleName to command
                    getItsLogger().info("Registered user command class: ${command.javaClass.simpleName}")
                }


                upsertCommand(data).queue()
                getItsLogger().info("Registered global user command: ${annotation.name}")
            }


            // MessageCommands
            for (clazz in reflections.getTypesAnnotatedWith(MessageCommand::class.java)) {
                val annotation = clazz.getAnnotation(MessageCommand::class.java)
                val data = Commands.message(annotation.name)

                if (clazz.simpleName !in loadedClasses) {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.trySetAccessible()

                    val command = constructor.newInstance()
                    loadedClasses += clazz.simpleName to command
                    getItsLogger().info("Registered message command class: ${command.javaClass.simpleName}")
                }


                upsertCommand(data).queue()
                getItsLogger().info("Registered global message command: ${annotation.name}")
            }
        }
        getItsLogger().info("Registered commands in $commandsTime")

        return this
    }
}