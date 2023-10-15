package de.coasterfreak.fantasyfrontiers.manager

import dev.fruxz.ascend.extension.logging.getItsLogger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import de.coasterfreak.fantasyfrontiers.annotations.MessageCommand
import de.coasterfreak.fantasyfrontiers.annotations.SlashCommand
import de.coasterfreak.fantasyfrontiers.annotations.UserCommand
import de.coasterfreak.fantasyfrontiers.interfaces.HasOptions
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommandGroups
import de.coasterfreak.fantasyfrontiers.interfaces.HasSubcommands
import de.coasterfreak.fantasyfrontiers.utils.Environment
import org.reflections8.Reflections
import kotlin.time.measureTime

object RegisterManager {

    private var loadedClasses = mapOf<String, Any>()

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