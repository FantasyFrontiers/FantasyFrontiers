package net.fantasyfrontiers.listeners.setup

import net.fantasyfrontiers.data.cache.ServerSettingsCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.db.discord.updateServerSettings
import net.fantasyfrontiers.data.model.discord.ServerSettings
import net.fantasyfrontiers.data.model.discord.SystemAnnouncementType
import net.fantasyfrontiers.utils.functions.withTestPermission
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu

class SystemAnnouncementSetup : ListenerAdapter() {

    private val buttonHandlers = mapOf(
        "ff-setup-advanced-system-announcement" to this::systemAnnouncementMenu,
        "ff-setup-advanced-system-announcement-deactivate" to this::onDeactivateAnnouncements
    )

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        if (!buttonHandlers.containsKey(componentId)) return@with
        if (!isFromGuild) return
        val serverSettings = ServerSettingsCache.get(guild!!.id)
        buttonHandlers[componentId]?.invoke(event, serverSettings)
    }

    private fun systemAnnouncementMenu(event: GenericComponentInteractionCreateEvent, serverSettings: ServerSettings) = with(event) {
        val languageCode = serverSettings.language

        val transDeactivated = TranslationCache.get(languageCode, "keywords.deactivated").toString()
        val transDeactivate = TranslationCache.get(languageCode, "keywords.deactivate").toString()
        val transThread = TranslationCache.get(languageCode, "keywords.thread").toString()
        val transChannel = TranslationCache.get(languageCode, "keywords.channel").toString()

        val transSystemChannel = TranslationCache.get(languageCode, "modals.advancedSetup.system-announcement.channel").toString()
        val whiteSpaceChar = "\u1CBC\u1CBC"

        val embed = EmbedBuilder()
            .setTitle(TranslationCache.get(languageCode, "modals.advancedSetup.system-announcement.title").toString())
            .setDescription("*" + TranslationCache.get(languageCode, "modals.advancedSetup.system-announcement.description.before")
                .toString() + "*\n\n" +

                    "${whiteSpaceChar.repeat(4)}$transDeactivated$whiteSpaceChar|$whiteSpaceChar$transThread$whiteSpaceChar|$whiteSpaceChar$transChannel\n" +
                    "**$transSystemChannel**: [${systemAnnouncementTypeToEmojis(serverSettings.systemAnnouncement.systemAnnouncementType)}]"
                    + (if (serverSettings.systemAnnouncement.systemAnnouncementType != SystemAnnouncementType.NONE) "$whiteSpaceChar<#${serverSettings.systemAnnouncement.announcementRoomChannelId}>" else "")
                    + "\n\n" +
                    "*" + TranslationCache.get(languageCode, "modals.advancedSetup.system-announcement.description.after")
                .toString() + "*"
            )
            .setColor(0x2b2d31)
            .build()

        val actionRows = listOf(
            ActionRow.of(
                Button.danger("ff-setup-advanced-system-announcement-deactivate", transDeactivate)
                    .withDisabled(serverSettings.systemAnnouncement.systemAnnouncementType == SystemAnnouncementType.NONE),
            ),
            ActionRow.of(
                EntitySelectMenu
                    .create("ff-setup-advanced-system-announcement-channel", setOf(EntitySelectMenu.SelectTarget.CHANNEL))
                    .setChannelTypes(ChannelType.TEXT, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD)
                    .build()
            )
        )


        if (isAcknowledged) {
            editMessageEmbeds(
                embed
            ).setComponents(actionRows).queue()
            return@with
        }

        replyEmbeds(
            embed
        ).setComponents(actionRows).setEphemeral(true).queue()
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) = with(event) {
        if (componentId != "ff-setup-advanced-system-announcement-channel") return@with

        val serverSettings = ServerSettingsCache.get(guild!!.id)
        val channel = values.firstOrNull()?.let { guild!!.getGuildChannelById(it.id) } ?: return@with

        val systemAnnouncementType = when(channel.type) {
            ChannelType.TEXT -> SystemAnnouncementType.CHANNEL
            ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PUBLIC_THREAD -> SystemAnnouncementType.THREAD
            else -> return@with
        }

        withTestPermission {
            val testMessage = (channel as GuildMessageChannel).sendMessage("Test message")
                .complete()
            testMessage.delete().queue()

            val newServerSettings = serverSettings.copy(
                systemAnnouncement = serverSettings.systemAnnouncement.copy(
                    systemAnnouncementType = systemAnnouncementType,
                    announcementRoomChannelId = channel.id
                )
            )

            ServerSettingsCache.put(newServerSettings)
            updateServerSettings(newServerSettings)

            systemAnnouncementMenu(event, newServerSettings)
        }
    }

    private fun onDeactivateAnnouncements(event: ButtonInteractionEvent, serverSettings: ServerSettings) = with(event) {
        val newServerSettings = serverSettings.copy(
            systemAnnouncement = serverSettings.systemAnnouncement.copy(
                systemAnnouncementType = SystemAnnouncementType.NONE,
                announcementRoomChannelId = null
            )
        )
        ServerSettingsCache.put(newServerSettings)
        updateServerSettings(newServerSettings)

        systemAnnouncementMenu(event, newServerSettings)
    }

    private fun systemAnnouncementTypeToEmojis(systemAnnouncementType: SystemAnnouncementType): String {
        val disabledEmoji = "<:iconCircle_grey:1164484851961954354>"
        val enabledEmoji = "<:iconCheck_beige:1164484847130120273>"
        val whiteSpaceReplacement = "\u1CBC\u1CBC"

        return when (systemAnnouncementType) {
            SystemAnnouncementType.NONE -> " $enabledEmoji | $disabledEmoji | $disabledEmoji ".replace(" ", whiteSpaceReplacement)
            SystemAnnouncementType.THREAD -> " $disabledEmoji | $enabledEmoji | $disabledEmoji ".replace(" ", whiteSpaceReplacement)
            SystemAnnouncementType.CHANNEL -> " $disabledEmoji | $disabledEmoji | $enabledEmoji ".replace(" ", whiteSpaceReplacement)
        }
    }
}