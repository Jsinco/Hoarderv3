package dev.jsinco.hoarder.commands

import dev.jsinco.hoarder.Hoarder
import org.bukkit.command.CommandSender

interface SubCommand {

    fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>)

    fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>?

    fun permission(): String?

    fun playerOnly(): Boolean
}