package de.coasterfreak.fantasyfrontiers.data.model.discord

import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel

@Serializable
enum class SystemAnnouncementType {
    NONE, CHANNEL, THREAD
}


@Serializable
data class SystemAnnouncement (
    val systemAnnouncementType: SystemAnnouncementType = SystemAnnouncementType.NONE,
    val announcementRoomChannelId: String? = null
) {

    fun getChannelOrNull(guild: Guild): GuildMessageChannel? {
        return announcementRoomChannelId?.let { guild.getGuildChannelById(it) as? GuildMessageChannel }
    }

}