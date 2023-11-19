package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        plugin.reloadConfig()
        Settings.reloadDataManager()
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        TODO("Not yet implemented")
    }

    override fun permission(): String? {
        TODO("Not yet implemented")
    }

    override fun playerOnly(): Boolean {
        TODO("Not yet implemented")
    }
}