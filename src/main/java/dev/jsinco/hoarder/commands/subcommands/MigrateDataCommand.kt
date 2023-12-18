package dev.jsinco.hoarder.commands.subcommands

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.commands.SubCommand
import dev.jsinco.hoarder.storage.DataMigrater
import dev.jsinco.hoarder.storage.StorageType
import org.bukkit.command.CommandSender

class MigrateDataCommand : SubCommand {
    override fun execute(plugin: Hoarder, sender: CommandSender, args: Array<out String>) {
        val newStorageType: StorageType = StorageType.valueOf(args[1].uppercase())
        DataMigrater(newStorageType).migrate()
        sender.sendMessage("debug: ${plugin.config.getString("storage.type")}")
    }

    override fun tabComplete(plugin: Hoarder, sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return StorageType.entries.map { it.name.lowercase() }.toMutableList()
    }

    override fun permission(): String? {
        return null
    }

    override fun playerOnly(): Boolean {
        return false
    }
}