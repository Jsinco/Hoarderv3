package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import org.bukkit.command.CommandSender

class SellCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        TODO("Not yet implemented")
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