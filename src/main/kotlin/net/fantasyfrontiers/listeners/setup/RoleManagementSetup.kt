package net.fantasyfrontiers.listeners.setup

import net.fantasyfrontiers.data.cache.ServerSettingsCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.db.discord.updateServerSettings
import net.fantasyfrontiers.data.model.discord.GuildRole
import net.fantasyfrontiers.data.model.discord.ServerSettings
import net.fantasyfrontiers.data.model.guild.Guilds
import net.fantasyfrontiers.utils.functions.withTestPermission
import net.fantasyfrontiers.utils.functions.formatNullableMentionCheck
import dev.fruxz.ascend.extension.container.lastOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class RoleManagementSetup : ListenerAdapter() {

    /**
     * Map of button handlers.
     */
    private val buttonHandlers = mapOf(
        "ff-setup-advanced-roles" to this::roleMenu,
        "ff-setup-advanced-roles-select-create-" to this::onGuildCreateRoleButtonClicked
    )

    /**
     * Map of string select handlers.
     *
     * This variable is a map that stores the string select handlers used in the RoleManagementSetup class.
     * The keys in the map are component IDs associated with the string select interactions, and the values are the corresponding handler functions.
     *
     * Example usage:
     *
     * ```
     * val stringSelectHandlers = mapOf(
     *     "ff-setup-advanced-roles-select" to this::onGuildSelect
     * )
     * ```
     *
     * @see RoleManagementSetup
     * @see RoleManagementSetup.onStringSelectInteraction
     * @see RoleManagementSetup.onGuildSelect
     */
    private val stringSelectHandlers = mapOf(
        "ff-setup-advanced-roles-select" to this::onGuildSelect
    )

    /**
     * Holds a map of entity select handlers.
     * The keys of the map are strings that identify the specific entity select interaction.
     * The values of the map are function references to the corresponding entity select handler methods.
     */
    private val entitySelectHandlers = mapOf(
        "ff-setup-advanced-roles-select-role-" to this::onGuildSelectRole
    )

    /**
     * Handles the button interaction event.
     *
     * @param event The button interaction event.
     */
    override fun onButtonInteraction(event: ButtonInteractionEvent) = with (event) {
        handleComponentInteraction(event)
    }

    private fun <T: GenericComponentInteractionCreateEvent> T.handleComponentInteraction(event: T) {
        val btnHandler = buttonHandlers.lastOrNull { componentId.startsWith(it.key) }
        if (btnHandler == null) return
        if (!isFromGuild) return
        val serverSettings = ServerSettingsCache.get(guild!!.id)

        val args = componentId.replace(btnHandler.key, "").split("-").toTypedArray()

        btnHandler.value.invoke(event, serverSettings, args)
    }

    /**
     * Method called when a string select interaction occurs.
     *
     * @param event The StringSelectInteractionEvent representing the interaction event.
     */
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) = with(event) {
        val stringSelectHandler = stringSelectHandlers.lastOrNull { componentId.startsWith(it.key) }
        if(stringSelectHandler == null) return@with
        if (!isFromGuild) return@with
        val serverSettings = ServerSettingsCache.get(guild!!.id)
        stringSelectHandlers[componentId]?.invoke(this, serverSettings)
    }

    /**
     * Handles the interaction when an entity is selected.
     *
     * @param event The EntitySelectInteractionEvent representing the interaction.
     */
    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) = with(event) {
        val entitySelectHandler = entitySelectHandlers.lastOrNull { componentId.startsWith(it.key) }
        if(entitySelectHandler == null) return@with
        if (!isFromGuild) return@with
        val serverSettings = ServerSettingsCache.get(guild!!.id)

        val args = componentId.replace(entitySelectHandler.key, "").split("-").toTypedArray()
        entitySelectHandler.value.invoke(this, serverSettings, args)
    }

    /**
     * Displays the role menu to the user.
     *
     * @param ctx The callback object used to send replies and interact with the user.
     * @param serverSettings The settings for the server.
     * @param args The optional arguments for the method.
     */
    private fun roleMenu(ctx: IReplyCallback, serverSettings: ServerSettings, vararg args: String) = with(ctx) {
        val languageCode = serverSettings.language

        val guildMap = Guilds.entries.map {
            val guildName = TranslationCache.get(languageCode, "guilds.${it.name.lowercase()}").toString()
            val guildRole = serverSettings.guildRoles.find { role -> role.guild == it }?.let { role ->
                guild?.getRoleById(role.roleId)?.asMention
            }
           Triple(it.name, guildName, guildRole)
        }

        if (isAcknowledged) {
            hook.editOriginalEmbeds(
                createRoleEmbed(languageCode, guildMap)
            ).setComponents(
                createAdvancedRolesSelectMenu(guildMap)
            ).queue()
            return@with
        }

        replyEmbeds(
            createRoleEmbed(languageCode, guildMap)
        ).addComponents(
            createAdvancedRolesSelectMenu(guildMap)
        ).setEphemeral(true).queue()
    }

    private fun createAdvancedRolesSelectMenu(guildMap: List<Triple<String, String, String?>>) =
        ActionRow.of(
            StringSelectMenu.create("ff-setup-advanced-roles-select")
                .apply {
                    guildMap.forEach { (plainName, guildName, _) ->
                        addOption(guildName, plainName.lowercase())
                    }
                }
                .build()
        )

    /**
     * Creates an EmbedBuilder instance for displaying role information.
     *
     * @param languageCode The language code for translation.
     * @param guildMap A list of Triple representing the guild name, role name, and guild role mention.
     * @return The built EmbedBuilder instance.
     */
    private fun createRoleEmbed(
        languageCode: String,
        guildMap: List<Triple<String, String, String?>>
    ) = EmbedBuilder()
        .setTitle(TranslationCache.get(languageCode, "modals.advancedSetup.roles.title").toString())
        .setColor(0x2b2d31)
        .setDescription(
            "*" + TranslationCache.get(languageCode, "modals.advancedSetup.roles.description.before")
                .toString() + "*\n\n" +
                    guildMap.joinToString("\n") { (_, guildName, guildRole) ->
                        "**$guildName**: ${formatNullableMentionCheck(guildRole)}"
                    }
                    + "\n\n" +
                    "*" + TranslationCache.get(languageCode, "modals.advancedSetup.roles.description.after")
                .toString() + "*"
        )
        .build()

    /**
     * Handles the selection of a guild by the user.
     *
     * @param ctx The StringSelectInteractionEvent object representing the event.
     * @param serverSettings The ServerSettings object representing the server settings.
     */
    private fun onGuildSelect(ctx: StringSelectInteractionEvent, serverSettings: ServerSettings) = with(ctx) {
        val languageCode = serverSettings.language
        val guildName = ctx.values.first()
        val guildRoleTemp = Guilds.valueOf(guildName.uppercase())
        val guildRole = serverSettings.guildRoles.find { role -> role.guild == guildRoleTemp }
        ctx.deferEdit().queue()

        hook.editOriginalEmbeds(
            EmbedBuilder()
                .setTitle(TranslationCache.get(languageCode, "modals.advancedSetup.roles.selected.title", mapOf(
                    "guild" to TranslationCache.get(languageCode, "guilds.${guildName.lowercase()}").toString()
                )).toString())
                .setColor(0x2b2d31)
                .setDescription(
                    TranslationCache.get(languageCode, "modals.advancedSetup.roles.selected.description", mapOf(
                        "guild" to TranslationCache.get(languageCode, "guilds.${guildName.lowercase()}").toString(),
                        "role" to formatNullableMentionCheck(guildRole?.let { guild?.getRoleById(it.roleId)?.asMention })
                    )).toString()
                )
                .build()
        ).setComponents(
            ActionRow.of(
                Button.secondary("ff-setup-advanced-roles-select-create-$guildName", TranslationCache.get(languageCode, "modals.advancedSetup.roles.selected.create").toString())
                    .withEmoji(Emoji.fromFormatted("✅")),
            ),
            ActionRow.of(
                EntitySelectMenu.create("ff-setup-advanced-roles-select-role-$guildName", EntitySelectMenu.SelectTarget.ROLE)
                    .setPlaceholder(TranslationCache.get(languageCode, "modals.advancedSetup.roles.selected.role.placeholder").toString())
                    .build()
            )
        ).queue()
    }

    /**
     * Handles the event when the Guild Create Role button is clicked.
     *
     * @param ctx The reply callback interface.
     * @param serverSettings The server settings.
     * @param args The arguments passed to the method.
     */
    private fun onGuildCreateRoleButtonClicked(ctx: IReplyCallback, serverSettings: ServerSettings, vararg args: String) = with(ctx) {
        if(args.size != 1) return@with
        deferReply(true).queue()
        val languageCode = serverSettings.language
        val guildName = args[0]
        val guildRoleTemp = Guilds.valueOf(guildName.uppercase())

        ctx.withTestPermission {
            val createdRole = guild?.createRole()
                ?.setName(TranslationCache.get(languageCode, "guilds.${guildRoleTemp.name.lowercase()}").toString())
                ?.setColor(guildRoleTemp.color)
                ?.complete()

            if (createdRole == null) {
                replyEmbeds(
                    EmbedBuilder()
                        .setTitle(TranslationCache.get(languageCode, "modals.errors.advancedSetup.roles.selected.create.title").toString())
                        .setColor(0xFF3333)
                        .setDescription(
                            TranslationCache.get(languageCode, "modals.errors.advancedSetup.roles.selected.create.description").toString()
                        )
                        .build()
                ).setEphemeral(true).queue()
                return@withTestPermission
            }

            val guildRole = serverSettings.guildRoles.find { role -> role.guild == guildRoleTemp }
                ?.copy(roleId = createdRole.id) ?: GuildRole(guildRoleTemp, createdRole.id)

            setupRoleManagement(serverSettings, guildRoleTemp, guildRole, ctx)
        }
    }

    /**
     * Handles the selection of a role in the guild.
     *
     * @param ctx The EntitySelectInteractionEvent containing the context of the interaction.
     * @param serverSettings The ServerSettings containing the settings for the server.
     * @param args The arguments passed along with the interaction.
     */
    private fun onGuildSelectRole(ctx: EntitySelectInteractionEvent, serverSettings: ServerSettings, vararg args: String) = with(ctx) {
        if(args.size != 1) return@with
        deferReply(true).queue()
        val guildName = args[0]
        val guildRoleTemp = Guilds.valueOf(guildName.uppercase())

        val roleId = values.first().id

        val guildRole = serverSettings.guildRoles.find { role -> role.guild == guildRoleTemp }
            ?.copy(roleId = roleId) ?: GuildRole(guildRoleTemp, roleId)

        setupRoleManagement(serverSettings, guildRoleTemp, guildRole, ctx)
    }

    /**
     * Sets up role management for a server.
     *
     * @param serverSettings The server settings.
     * @param guildRoleTemp The temporary guild role.
     * @param guildRole The guild role to be added.
     * @param ctx The reply callback.
     */
    private fun setupRoleManagement(
        serverSettings: ServerSettings,
        guildRoleTemp: Guilds,
        guildRole: GuildRole,
        ctx: IReplyCallback
    ) {
        val guildRoles = serverSettings.guildRoles.filter { role -> role.guild != guildRoleTemp }.toMutableList()
        guildRoles += guildRole

        val newServerSettings = serverSettings.copy(guildRoles = guildRoles)
        ServerSettingsCache.put(newServerSettings)
        updateServerSettings(newServerSettings)

        roleMenu(ctx, newServerSettings)
    }
}