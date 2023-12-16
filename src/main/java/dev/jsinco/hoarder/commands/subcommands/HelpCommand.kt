package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.objects.LangMsg
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HelpCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        sender.sendMessage(LangMsg("commands.help").getMsgSendSound(sender as? Player))
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return null
    }

    override fun permission(): String? {
        return "hoarder.command.help"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}