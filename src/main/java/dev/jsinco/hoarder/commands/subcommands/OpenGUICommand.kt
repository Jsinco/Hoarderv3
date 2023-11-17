package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.FileManager
import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.gui.GUICreator
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class OpenGUICommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        sender as Player
        if (args.size < 2) return
        val path = "guis/${args[1]}"
        val guiCreator = GUICreator(path)
        sender.openInventory(guiCreator.inventory)
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {

        return Util.listFilesForFolder(File(plugin.dataFolder, "guis")).map { it.name }.toMutableList()
    }

    override fun permission(): String? {
        return null
    }

    override fun playerOnly(): Boolean {
        return true
    }
}