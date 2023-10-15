package one.devsky.boilerplates.listeners

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import one.devsky.boilerplates.annotations.SlashCommand
import one.devsky.boilerplates.interfaces.HasOptions


@SlashCommand("modal", "Erzeugt ein Test Modal")
class ModalTest : ListenerAdapter(), HasOptions {

    override fun getOptions(): List<OptionData> {
        return listOf(
            OptionData(OptionType.STRING, "type", "Type of modal", true)
                .addChoice("Email", "email")
                .addChoice("Username", "username")
        )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) = with(event) {
        if(name != "modal") return@with

        val type = getOption("type")?.asString ?: "email"

        val email = TextInput.create("email", "Email", TextInputStyle.SHORT)
            .setPlaceholder("Enter your E-mail")
            .setMinLength(10)
            .setMaxLength(100) // or setRequiredRange(10, 100)
            .build()

        val username = TextInput.create("username", "Username", TextInputStyle.SHORT)
            .setPlaceholder("Enter your Username")
            .setMinLength(3)
            .setMaxLength(100) // or setRequiredRange(10, 100)
            .build()

        val body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
            .setPlaceholder("Your concerns go here")
            .setMinLength(30)
            .setMaxLength(1000)
            .build()

        val modal: Modal = Modal.create("modal", "Support")
            .addComponents(
                if (type == "username") ActionRow.of(username) else ActionRow.of(email),
                ActionRow.of(body))
            .build()

        replyModal(modal).queue()
    }


    override fun onModalInteraction(event: ModalInteractionEvent) = with(event) {
        if(modalId != "modal") return@with

        val email = getValue("email")?.asString
        val username = getValue("username")?.asString
        val body = getValue("body")?.asString

        val message = if (username != null)
            "Your username is $username"
        else
            "Your email is $email"

        reply("$message and your body is $body").setEphemeral(true).queue()
    }


}