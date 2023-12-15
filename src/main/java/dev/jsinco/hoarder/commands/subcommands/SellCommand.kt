package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.SellingManager
import dev.jsinco.hoarder.objects.LangMsg
import org.bukkit.Bukkit
import org.bukkit.block.BlockFace
import org.bukkit.block.Container
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SellCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        val player = sender as Player
        val type: String = if (args.size >= 2) args[1] else "inventory"

        val sellingManager: SellingManager = when (type.lowercase()) {
            "container" -> {
                val block = player.getTargetBlockExact(6) ?: run { LangMsg("actions.sell-none").sendMessage(player); return }
                val playerInteractEvent = PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.inventory.itemInMainHand, block, BlockFace.NORTH)
                Bukkit.getPluginManager().callEvent(playerInteractEvent)

                if (playerInteractEvent.useInteractedBlock() == Event.Result.DENY || block.state !is Container) {
                    LangMsg("actions.sell-none").sendMessage(player)
                    return
                }
                SellingManager(player, (block.state as Container).inventory)
            }
            else -> SellingManager(player, player.inventory)
        }
        sellingManager.sellActiveItem()
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        if (args.size >= 2) {
            return mutableListOf("container", "inventory")
        }
        return null
    }

    override fun permission(): String {
        return "hoarder.command.sell"
    }

    override fun playerOnly(): Boolean {
        return true
    }
}