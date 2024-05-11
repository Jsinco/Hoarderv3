package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class SetPointsCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) return
        val player = Bukkit.getOfflinePlayer(args[1])
        val points = args[2].toIntOrNull() ?: return

        val dataManager = Settings.getDataManger()

        dataManager.setPoints(player.uniqueId.toString(), points)
        sender.sendMessage("§aSuccessfully set ${player.name}'s points to $points")
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        when (args.size) {
            3 -> return mutableListOf("<points>")
        }
        return null
    }

    override fun permission(): String {
        return "hoarder.command.setpoints"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}