package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.utilities.Messages.getMsgConsoleSender
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        plugin.logger.info(getMsgConsoleSender("reload.start"))
        plugin.reloadConfig()
        Settings.reloadDataManager()
        Hoarder.generateFiles()
        HoarderEvent(plugin).reloadHoarderEvent()
        // TODO: reload etc
        plugin.logger.info(getMsgConsoleSender("reload.end"))
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return null
    }

    override fun permission(): String {
        return "hoarder.command.reload"
    }

    override fun playerOnly(): Boolean {
        return false
    }
}