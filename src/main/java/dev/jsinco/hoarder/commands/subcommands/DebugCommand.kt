package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DebugCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {

        if (args.size >= 2 && args[1] == "start") {
            sender.sendMessage("starting")
            HoarderEvent(plugin).startHoarderEvent(10000)
            return
        } else if (args.size >= 2 && args[1] == "end") {
            sender.sendMessage("ending")
            HoarderEvent(plugin).endHoarderEvent()
            HoarderEvent(plugin).startHoarderEvent(10000)
            return
        } else if (args.size >= 2 && args[1] == "addt") {
            Settings.getDataManger().addClaimableTreasures((sender as Player).uniqueId.toString(), 1)
            return
        }

        val dataManager = Settings.getDataManger()
        dataManager.setEventMaterial(Material.GOLD_BLOCK)
        plugin.logger.info("material: ${dataManager.getEventMaterial()}")
        dataManager.setEventEndTime(2000)
        plugin.logger.info("endtime: ${dataManager.getEventEndTime()}")
        dataManager.setEventSellPrice(100.0)
        plugin.logger.info("sellprice: ${dataManager.getEventSellPrice()}")
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return null
    }

    override fun permission(): String? {
        return null
    }

    override fun playerOnly(): Boolean {
        return false
    }
}