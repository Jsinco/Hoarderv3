package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.Msg
import dev.jsinco.hoarder.utilities.Util
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TreasureCommand : SubCommand {

    val allIdentifiers = Settings.getDataManger().getAllTreasureItems()?.map { it.identifier }?.toMutableList()

    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        val dataManager = Settings.getDataManger()
        val player = sender as Player

        when (args[1].lowercase()) {
            "add" -> {
                val itemStack = player.inventory.itemInMainHand

                if (args.size < 3) {
                    Msg("commands.treasure.add.missing-args").sendMessage(player)
                    return
                }
                val weight = args[2].toIntOrNull() ?: return

                val identifier = if (Settings.autoIdentify() && args.size < 4) {
                    Util.generateItemIdentifier(itemStack)
                } else if (args.size >= 4) {
                    args[3].lowercase()
                } else {
                    Msg("commands.treasure.add.missing-args-no-auto-identifier").sendMessage(player)
                    return
                }

                allIdentifiers?.add(identifier)
                dataManager.addTreasureItem(identifier, weight, itemStack)
                player.sendMessage(Msg("commands.treasure.add.success").getMsgSendSound(player).format(identifier, args[2]))
            }

            "edit" -> {
                if (args.size < 3) {
                    Msg("commands.treasure.edit.missing-args").sendMessage(player)
                    return
                }

                val identifier = args[2]
                val newWeight = args[3].toIntOrNull() ?: return
                val newIdentifier = if (args.size >= 5) args[4] else identifier

                allIdentifiers?.remove(identifier)
                allIdentifiers?.add(newIdentifier)
                dataManager.modifyTreasureItem(identifier, newWeight, newIdentifier)
                player.sendMessage(Msg("commands.treasure.edit.success").getMsgSendSound(player).format(identifier, newIdentifier, newWeight))
            }

            "delete" -> {
                if (args.size < 3) {
                    Msg("commands.treasure.delete.missing-args").sendMessage(player)
                    return
                }

                val identifier = args[2]
                allIdentifiers?.remove(identifier)
                dataManager.removeTreasureItem(identifier)
                player.sendMessage(Msg("treasure.delete.success").getMsgSendSound(player).format(identifier))
            }
        }
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        if (args.size == 2) {
            return mutableListOf("add", "edit", "delete")
        }

        when (args[1].lowercase()) {
            "add" -> {
                when (args.size) {
                    3 -> return mutableListOf("<chance/weight>")
                    4 -> return mutableListOf("<identifier>")
                }
            }

            "edit" -> {
                when (args.size) {
                    3 -> return allIdentifiers
                    4 -> return mutableListOf("<chance/weight>")
                    5 -> return mutableListOf("<identifier>")
                }
            }

            "delete" -> {
                when (args.size) {
                    3 -> return allIdentifiers
                }
            }
        }
        return null
    }

    override fun permission(): String {
        return "hoarder.command.treasure"
    }

    override fun playerOnly(): Boolean {
        return true
    }
}