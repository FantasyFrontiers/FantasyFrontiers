package de.coasterfreak.fantasyfrontiers.manager

import de.coasterfreak.fantasyfrontiers.annotations.MessageCommand
import de.coasterfreak.fantasyfrontiers.annotations.SlashCommand
import de.coasterfreak.fantasyfrontiers.annotations.UserCommand
import de.coasterfreak.fantasyfrontiers.data.cache.TranslationCache
import de.coasterfreak.fantasyfrontiers.interfaces.HasOptions
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommandGroups
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommands
import de.coasterfreak.fantasyfrontiers.utils.functions.getDiscordLocale
import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.reflections8.Reflections
import java.util.*
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

                val translationsName = TranslationCache.getTranslationsFor("command.${annotation.name}.name").map {
                    getDiscordLocale(it.key) to (it.value?.message ?: annotation.name)
                }.toMap()
                val translationsDescription = TranslationCache.getTranslationsFor("command.${annotation.name}.description").map {
                    getDiscordLocale(it.key) to (it.value?.message ?: annotation.description)
                }.toMap()

                val data = Commands.slash(annotation.name, annotation.description)
                    .apply {
                        setNameLocalizations(translationsName)
                        setDescriptionLocalizations(translationsDescription)
                    }

                if (clazz.simpleName !in loadedClasses) {
                    val constructor = clazz.getDeclaredConstructor()
                    constructor.trySetAccessible()

                    val command = constructor.newInstance()
                    loadedClasses += clazz.simpleName to command
                    getItsLogger().info("Registered command class: ${command.javaClass.simpleName}")
                }

                val command = loadedClasses[clazz.simpleName]

                if (command is HasOptions) {
                    val options = command.getOptions().map {
                        val optionTranslationsName = TranslationCache.getTranslationsFor("command.${annotation.name}.options.${it.name}.name").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: it.name)
                        }.toMap()
                        val optionTranslationsDescription = TranslationCache.getTranslationsFor("command.${annotation.name}.options.${it.name}.description").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: it.description)
                        }.toMap()

                        it.apply {
                            setNameLocalizations(optionTranslationsName)
                            setDescriptionLocalizations(optionTranslationsDescription)
                        }
                    }

                    data.addOptions(options)
                }

                if (command is HasSubcommandGroups) {
                    val subcommandGroups = command.getChoices().map { choiceData ->
                        val choiceTranslationName = TranslationCache.getTranslationsFor("command.${annotation.name}.choices.${choiceData.name}.name").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: choiceData.name)
                        }.toMap()

                        val choiseTranslationDescription = TranslationCache.getTranslationsFor("command.${annotation.name}.choices.${choiceData.name}.description").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: choiceData.description)
                        }.toMap()

                        choiceData.apply {
                            setNameLocalizations(choiceTranslationName)
                            setDescriptionLocalizations(choiseTranslationDescription)
                        }
                    }

                    data.addSubcommandGroups(subcommandGroups)
                }

                if (command is HasSubcommands) {
                    val subcommands = command.getSubCommands().map { subcommandData ->
                        val subcommandTranslationName = TranslationCache.getTranslationsFor("command.${annotation.name}.subcommands.${subcommandData.name}.name").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: subcommandData.name)
                        }.toMap()

                        val subcommandTranslationDescription = TranslationCache.getTranslationsFor("command.${annotation.name}.subcommands.${subcommandData.name}.description").map { entry ->
                            getDiscordLocale(entry.key) to (entry.value?.message ?: subcommandData.description)
                        }.toMap()

                        subcommandData.apply {
                            setNameLocalizations(subcommandTranslationName)
                            setDescriptionLocalizations(subcommandTranslationDescription)
                        }
                    }

                    data.addSubcommands(subcommands)
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