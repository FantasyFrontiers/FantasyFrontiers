package net.fantasyfrontiers.utils.extensions

import net.fantasyfrontiers.data.model.guild.GuildCard
import net.fantasyfrontiers.data.model.guild.Guilds

/**
 * Checks if any guild card in the list belongs to the specified guild.
 *
 * @param guild The guild to check for.
 * @return true if at least one guild card in the list belongs to the specified guild, false otherwise.
 */
fun List<GuildCard>.containsGuild(guild: Guilds) = any { it.guild == guild }

/**
 * Filters the list of [GuildCard] objects to find the first card that belongs to the specified [guild].
 *
 * @param guild The guild to search for.
 * @return The [GuildCard] object that belongs to the specified [guild].
 * @throws NoSuchElementException If no matching guild card is found.
 */
fun List<GuildCard>.getGuild(guild: Guilds) = first { it.guild == guild }