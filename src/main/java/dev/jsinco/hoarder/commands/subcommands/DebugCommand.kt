package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Material
import org.bukkit.command.CommandSender

class DebugCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        val dataManager = Settings.getDataManger()
        dataManager.setEventMaterial(Material.GOLD_BLOCK)
        plugin.logger.info("material: ${dataManager.getEventMaterial()}")
        dataManager.setEventEndTime(2000)
        plugin.logger.info("endtime: ${dataManager.getEventEndTime()}")
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