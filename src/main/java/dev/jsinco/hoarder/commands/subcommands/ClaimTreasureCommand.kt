package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.objects.HoarderPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClaimTreasureCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        sender as Player

        val hoarderPlayer = HoarderPlayer(sender.uniqueId.toString())

        hoarderPlayer.claimTreasure(1)
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return null
    }

    override fun permission(): String? {
        return "hoarder.command.claim"
    }

    override fun playerOnly(): Boolean {
        return true
    }
}