package net.fantasyfrontiers.listeners.game.town

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.fantasyfrontiers.data.cache.CharacterCache
import net.fantasyfrontiers.data.cache.ServerSettingsCache
import net.fantasyfrontiers.data.cache.TranslationCache
import net.fantasyfrontiers.data.db.player.saveCharacter
import net.fantasyfrontiers.data.model.guild.Guilds
import net.fantasyfrontiers.data.model.items.Item
import net.fantasyfrontiers.data.model.items.ItemStack
import net.fantasyfrontiers.data.model.town.Location
import net.fantasyfrontiers.data.model.town.LocationActions
import net.fantasyfrontiers.data.model.town.SpecialLocation
import net.fantasyfrontiers.utils.functions.checkIfAlreadyTraveling
import java.util.*

class TownActionListener : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) = with(event) {
        if(!componentId.startsWith("ff-town-menu-action-")) return@with

        var character = CharacterCache.get(event.user.id) ?: return@with
        val languageCode = character.language

        if (checkIfAlreadyTraveling(event, languageCode)) return@with

        event.deferEdit().queue()

        val location = character.location
        val town = location.town

        val action = LocationActions.valueOf(componentId.removePrefix("ff-town-menu-action-"))

        when (action) {
            LocationActions.JOIN_GUILD -> {
                val guild = when (location.specialLocation) {
                    SpecialLocation.ADVENTURERS_GUILD -> Guilds.ADVENTURERS_GUILD
                    SpecialLocation.BLACKSMITHS_GUILD -> Guilds.BLACKSMITHS_GUILD
                    SpecialLocation.HERB_GUILD -> Guilds.HERBOLOGIES_GUILD
                    SpecialLocation.MERCHANTS_GUILD -> Guilds.MERCHANTS_GUILD
                    else -> return@with
                }
                val guildName = TranslationCache.get(languageCode, "guilds.${guild.name.lowercase()}").toString()

                if(character.joinGuild(guild)) {
                    val joinedGuildMessage = TranslationCache.get(languageCode, "town.menu.joined.guild", mapOf("guild" to guildName)).toString()
                    val updatedCharacter = CharacterCache.get(event.user.id) ?: return@with

                    // Test for discord roles
                    Thread {
                        val mutableServers = user.mutualGuilds
                        for (server in mutableServers) {
                            val serverSettings = server.id.let { ServerSettingsCache.get(it) }
                            val guildRole = serverSettings.getGuildRole(guild) ?: continue
                            try {
                                val role = server.getRoleById(guildRole) ?: continue
                                server.addRoleToMember(user, role).queue()
                            } catch (e: Exception) {
                                println("Failed to add role to member `${user.id}`/`${user.name}` on `${server.id}`/`${server.name}`: ${e.message}")
                            }
                        }
                    }.start()

                    showTownMenu(updatedCharacter, joinedGuildMessage)
                    return@with
                }

                val alreadyJoinedGuildMessage = TranslationCache.get(languageCode, "modals.errors.already.joined.guild", mapOf("guild" to guildName)).toString()
                showTownMenu(character, alreadyJoinedGuildMessage)
                return@with
            }
            LocationActions.HARVEST_GARDEN -> {
                val possibleItems = Item.getHarvestables(town)
                val random = Random()

                val amountDifferentItems = random.nextInt(2) + 1
                val items = mutableListOf<ItemStack>()

                for (i in 0 until amountDifferentItems) {
                    val item = possibleItems[random.nextInt(possibleItems.size)]
                    var amount = random.nextInt(3) + 1

                    val drops = character.inventory.addItem(ItemStack(item, amount))
                    amount -= drops

                    if (amount > 0) {
                        items.add(ItemStack(item, amount))
                    }
                }

                if (items.isEmpty()) {
                    val noHarvestMessage = TranslationCache.get(languageCode, "town.menu.no.harvest").toString()
                    showTownMenu(character, noHarvestMessage)
                    return@with
                }

                CharacterCache.put(character)
                saveCharacter(character)

                val itemDescriptions = items.map { itemStack ->
                    "- ${TranslationCache.get(languageCode, itemStack.item.getTranslatableName())} **x${itemStack.amount}**"
                }

                val harvestMessage = TranslationCache.get(languageCode, "town.menu.harvested").toString() + "\n" + itemDescriptions.joinToString("\n")

                showTownMenu(character, harvestMessage)
                return@with
            }
            LocationActions.SELL -> {
                val items = character.inventory.sellAll()

                val itemDescriptions = items.map { itemStack ->
                    "**${itemStack.amount}x** ${TranslationCache.get(languageCode, itemStack.item.getTranslatableName())}"
                }

                val sellMessage = TranslationCache.get(languageCode, "town.menu.sell.all", mapOf(
                    "amount" to itemDescriptions.joinToString(", ")
                )).toString()

                CharacterCache.put(character)
                saveCharacter(character)

                showTownMenu(character, sellMessage)
                return@with
            }
            else -> {}
        }


        showTownMenu(character)
    }

}